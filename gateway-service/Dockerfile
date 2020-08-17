#FROM codenvy/jdk8_maven3_tomcat8:latest
FROM java:8
RUN mkdir code
ADD ./target/gateway-service-1.0-SNAPSHOT.jar /code
WORKDIR /code
EXPOSE 8000
CMD ["java", "-jar","gateway-service-1.0-SNAPSHOT.jar"]