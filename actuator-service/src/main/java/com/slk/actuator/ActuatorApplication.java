package com.slk.actuator;


import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
@EnableAdminServer
public class ActuatorApplication {
  public static void main(String[] args) {
    SpringApplication.run(ActuatorApplication.class, args);
  }
}
