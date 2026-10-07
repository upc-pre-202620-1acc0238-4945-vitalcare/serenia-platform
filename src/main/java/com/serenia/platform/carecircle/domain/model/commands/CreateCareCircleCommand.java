package com.serenia.platform.carecircle.domain.model.commands;

import java.util.UUID;

/**
 * Intention of creating the care circle of a newly registered older adult.
 */
public record CreateCareCircleCommand(UUID olderAdultId) {
}
