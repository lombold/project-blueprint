package com.projectname;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProjectNameApplication {
  static void main(final String[] args) {
    SpringApplication.run(ProjectNameApplication.class, args);
  }
}
