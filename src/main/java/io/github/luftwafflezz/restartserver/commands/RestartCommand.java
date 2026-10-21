package io.github.luftwafflezz.restartserver.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.luftwafflezz.restartserver.RestartServer;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.PermissionLevel;

import java.io.IOException;
import java.util.ArrayList;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

public class RestartCommand {
	public static final String NAME = "restart";
	public static final PermissionLevel PERMISSION_LEVEL = PermissionLevel.OWNERS;

	public static LiteralArgumentBuilder<CommandSourceStack> register() {
		return Commands.literal(NAME).executes(ctx -> execute(ctx.getSource())).requires((ctx) -> Permissions.check(ctx, "restart_server.commands."+NAME, PERMISSION_LEVEL));
	}

	public static int execute(CommandSourceStack source) {
		if (RestartServer.config.sendRestartMessage) {
			// Send restart message to console
			source.getServer().sendSystemMessage(Component.literal("[Restart Server] " + RestartServer.config.restartMessage).withStyle(ChatFormatting.YELLOW));
			// Disconnect all players with the restart message
			new ArrayList<>(source.getServer().getPlayerList().getPlayers()).forEach(player -> player.connection.disconnect(Component.literal(RestartServer.config.restartMessage)));
		}

		if (RestartServer.config.runRestartScript) {
			try {
				// Start restart script process
				if (RestartServer.config.openInTerminal) {
					Runtime.getRuntime().exec(String.join(" ", RestartServer.config.terminalStartCommand, RestartServer.config.restartScriptPath));
				} else {
					Runtime.getRuntime().exec(RestartServer.config.restartScriptPath);
				}

				// Send detailed restart message to console
				source.getServer().sendSystemMessage(Component.literal("[Restart Server] " + String.format("Restarting server using script '%s'...", RestartServer.config.restartScriptPath)).withStyle(ChatFormatting.YELLOW));

				// Stop server
				if (RestartServer.config.stopServer) {
					source.getServer().halt(false);
				}

				return 0;
			} catch (IOException | RuntimeException e) {
				RestartServer.LOGGER.info("[Restart Server] " + getStackTrace(e));

				// Send restart failed message to console and all players
				source.getServer().getPlayerList().broadcastSystemMessage(Component.literal("[Restart Server] " + RestartServer.config.restartFailedMessage).withStyle(ChatFormatting.RED), RestartServer.config.sendRestartMessageInActionbar);

				return 1;
			}
		} else {
			// Stop server
			if (RestartServer.config.stopServer) {
				source.getServer().halt(false);
			}

			return 0;
		}
	}
}
