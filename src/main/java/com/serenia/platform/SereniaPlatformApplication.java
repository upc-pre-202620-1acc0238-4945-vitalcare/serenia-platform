package com.serenia.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.TimeZone;

/**
 * Bootstrap class for the Learning Center Platform application.
 *
 * <p>Initializes Spring Boot auto-configuration and JPA auditing infrastructure.</p>
 */
@EnableJpaAuditing
@SpringBootApplication
public class SereniaPlatformApplication {

    public static void main(String[] args) {
        // Instants and times are stored in UTC, so the JVM must not depend on the zone of the machine
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication.run(SereniaPlatformApplication.class, args);
    }
}
