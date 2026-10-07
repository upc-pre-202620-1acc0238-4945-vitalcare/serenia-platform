package com.serenia.platform.dailycheckin.interfaces.rest.transform;

import com.serenia.platform.dailycheckin.domain.model.commands.DisableSimplifiedModeCommand;
import com.serenia.platform.dailycheckin.domain.model.commands.EnableSimplifiedModeCommand;
import com.serenia.platform.dailycheckin.interfaces.rest.resources.UpdateSimplifiedModeResource;

import java.util.UUID;

/**
 * Converts an {@link UpdateSimplifiedModeResource} into the enable or disable command,
 * according to the received value.
 */
public class SimplifiedModeCommandFromResourceAssembler {

    /** Either an {@link EnableSimplifiedModeCommand} or a {@link DisableSimplifiedModeCommand}. */
    public sealed interface SimplifiedModeCommand permits Enable, Disable {
    }

    public record Enable(EnableSimplifiedModeCommand command) implements SimplifiedModeCommand {
    }

    public record Disable(DisableSimplifiedModeCommand command) implements SimplifiedModeCommand {
    }

    public static SimplifiedModeCommand toCommandFromResource(UUID olderAdultId, UpdateSimplifiedModeResource resource) {
        return resource.enabled()
                ? new Enable(new EnableSimplifiedModeCommand(olderAdultId))
                : new Disable(new DisableSimplifiedModeCommand(olderAdultId));
    }
}
