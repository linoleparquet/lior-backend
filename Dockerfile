FROM openjdk:8-jdk-alpine
#RUN addgroup -S group && adduser -S user -G group
#USER user:group
RUN mkdir -p /h2
EXPOSE 8080
ARG JAR_FILE=target/lior-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]