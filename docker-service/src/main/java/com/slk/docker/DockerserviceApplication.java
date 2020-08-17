package com.slk.docker;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.slk")

//@EnableAspectJAutoProxy
public class DockerserviceApplication {
  public static void main(String[] args) {
    SpringApplication.run(DockerserviceApplication.class, args);
  }
}
