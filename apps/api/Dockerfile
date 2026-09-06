FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S lab && adduser -S lab -G lab
COPY target/cloud-event-lab-api-*.jar /app/app.jar
USER lab
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
