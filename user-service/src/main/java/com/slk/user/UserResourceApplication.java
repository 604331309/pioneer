package com.slk.user;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class UserResourceApplication {
  public static void main(String[] args) {
    SpringApplication.run(UserResourceApplication.class, args);
  }
}
