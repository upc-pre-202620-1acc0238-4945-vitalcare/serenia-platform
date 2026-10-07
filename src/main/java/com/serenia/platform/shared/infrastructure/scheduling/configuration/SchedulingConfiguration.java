package com.serenia.platform.shared.infrastructure.scheduling.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables the periodic jobs declared by the bounded contexts with {@code @Scheduled}.
 */
@Configuration
@EnableScheduling
public class SchedulingConfiguration {
}
