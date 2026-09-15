FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Patch Alpine OS packages shipped in the base image (Trivy image gate).
RUN apk upgrade --no-cache
RUN addgroup -S lab && adduser -S lab -G lab
COPY target/cloud-event-lab-api-*.jar /app/app.jar
USER lab
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
