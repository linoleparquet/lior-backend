# Building the application using maven
FROM maven:3.8.1-openjdk-17-slim as builder
COPY src /src
COPY pom.xml /pom.xml
RUN mvn package
RUN mv $(find /target -name "*.jar") /app.jar

# Running the application
FROM openjdk:11-jre-slim
COPY --from=builder /app.jar /app.jar
EXPOSE 8080
CMD ["java","-jar","/app.jar"]