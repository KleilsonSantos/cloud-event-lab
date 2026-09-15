package dev.kleilson.cloudeventlab.adapters.aws;

import dev.kleilson.cloudeventlab.application.port.ObjectStoragePort;
import jakarta.annotation.PreDestroy;
import java.net.URI;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

/**
 * Object storage via S3 API against LocalStack (LOCAL EMULATOR) or a real endpoint when configured.
 * Not AWS production parity — see docs/aws/README.md.
 */
@Component
@Profile("aws")
@ConditionalOnProperty(name = "lab.cloud.provider", havingValue = "aws")
public class S3ObjectStorageAdapter implements ObjectStoragePort {

  private final S3Client s3;
  private final String bucket;

  @Autowired
  public S3ObjectStorageAdapter(
      @Value("${lab.aws.endpoint}") String endpoint,
      @Value("${lab.aws.region:us-east-1}") String region,
      @Value("${lab.aws.s3.bucket:cloud-event-lab}") String bucket,
      @Value("${lab.aws.access-key-id:test}") String accessKeyId,
      @Value("${lab.aws.secret-access-key:test}") String secretAccessKey,
      @Value("${lab.aws.use-static-credentials:true}") boolean useStaticCredentials) {
    this.bucket = bucket;
    var builder =
        S3Client.builder()
            .endpointOverride(URI.create(endpoint))
            .region(Region.of(region))
            .forcePathStyle(true);
    if (useStaticCredentials) {
      builder.credentialsProvider(
          StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)));
    } else {
      builder.credentialsProvider(DefaultCredentialsProvider.builder().build());
    }
    this.s3 = builder.build();
    ensureBucket();
  }

  /** Test / manual wiring without Spring. */
  public S3ObjectStorageAdapter(S3Client s3, String bucket) {
    this.s3 = s3;
    this.bucket = bucket;
    ensureBucket();
  }

  private void ensureBucket() {
    try {
      s3.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
    } catch (NoSuchBucketException e) {
      s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
    } catch (S3Exception e) {
      if (e.statusCode() == 404 || "NotFound".equals(e.awsErrorDetails().errorCode())) {
        s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
      } else {
        throw e;
      }
    }
  }

  @Override
  public String put(String key, byte[] content, String contentType) {
    s3.putObject(
        PutObjectRequest.builder()
            .bucket(bucket)
            .key(key)
            .contentType(contentType == null ? "application/octet-stream" : contentType)
            .build(),
        RequestBody.fromBytes(content));
    return key;
  }

  @Override
  public Optional<byte[]> get(String key) {
    try {
      return Optional.of(
          s3.getObjectAsBytes(GetObjectRequest.builder().bucket(bucket).key(key).build())
              .asByteArray());
    } catch (NoSuchKeyException e) {
      return Optional.empty();
    } catch (S3Exception e) {
      if (e.statusCode() == 404) {
        return Optional.empty();
      }
      throw e;
    }
  }

  @Override
  public void delete(String key) {
    s3.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
  }

  @PreDestroy
  void close() {
    s3.close();
  }
}
