#FROM codenvy/jdk8_maven3_tomcat8:latest
FROM java:8
RUN mkdir code
ADD ./target/eureka-0.0.1-SNAPSHOT.jar /code
WORKDIR /code
EXPOSE 8761
CMD ["java", "-jar","eureka-0.0.1-SNAPSHOT.jar"]