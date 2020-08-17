package com.slk.drools;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients(basePackages = "com.slk")

public class DroolsApplication {
  public static void main(String[] args) {
    SpringApplication.run(DroolsApplication.class, args);
  }
}
