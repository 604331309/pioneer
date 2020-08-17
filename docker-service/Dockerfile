#FROM codenvy/jdk8_maven3_tomcat8:latest
FROM java:8
RUN mkdir code
ADD ./target/docker-service-1.0-SNAPSHOT.jar /code
WORKDIR /code
EXPOSE 8011
CMD ["java", "-jar","docker-service-1.0-SNAPSHOT.jar"]