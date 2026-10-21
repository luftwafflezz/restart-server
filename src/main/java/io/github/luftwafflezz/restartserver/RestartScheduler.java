package io.github.luftwafflezz.restartserver;

import io.github.luftwafflezz.restartserver.commands.RestartCommand;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

import java.time.Instant;

public class RestartScheduler {
	public boolean isRestartScheduled = false;
	public long nextRestart = -1;

	public int lastWarning = -1;

	public boolean isRestartIfNoPlayersHaveBeenOnlineScheduled = false;
	public long nextRestartIfNoPlayersHaveBeenOnline = -1;

	public void onTick(MinecraftServer server) {
		// Nothing is left to schedule once the server is stopping
		if (!server.isRunning()) {
			return;
		}

		restartIntervalCounter(server);

		// Don't restart a second time in the same tick if the restart above already stopped the server
		if (!server.isRunning()) {
			return;
		}

		RestartIfNoPlayersHaveBeenOnlineCounter(server);
	}

	private void restartIntervalCounter(MinecraftServer server) {
		if (RestartServer.config.restartInterval <= 0) {
			return;
		}

		// Get current epoch time
		long now = Instant.now().getEpochSecond();

		if (isRestartScheduled) {
			if (nextRestart <= now) {
				// Restart server
				RestartCommand.execute(server.createCommandSourceStack());
				// Schedule the next restart, for when the restart doesn't stop the server (stopServer is disabled, or the restart script failed)
				isRestartScheduled = false;
			} else {
				long difference = nextRestart - now;
				if (difference % 60 != 0) return;
				int minutesUntilRestart = (int) difference / 60;
				if (minutesUntilRestart != lastWarning) {
					lastWarning = minutesUntilRestart;
					if (minutesUntilRestart > RestartServer.config.restartWarningCount) return;
					server.getPlayerList().broadcastSystemMessage(Component.literal(String.format(RestartServer.config.restartWarningMessage, minutesUntilRestart)).withStyle(ChatFormatting.YELLOW), false);
				}
			}
		} else {
			// Schedule restart
			nextRestart = now + RestartServer.config.restartInterval;
			isRestartScheduled = true;
		}
	}

	private void RestartIfNoPlayersHaveBeenOnlineCounter(MinecraftServer server) {
		if (!RestartServer.config.restartIfNoPlayersHaveBeenOnline) {
			return;
		}

		// Get current epoch time
		long now = Instant.now().getEpochSecond();

		if (isRestartIfNoPlayersHaveBeenOnlineScheduled) {
			if (server.getPlayerCount() > 0) {
				// Unschedule no players restart
				nextRestartIfNoPlayersHaveBeenOnline = -1;
				isRestartIfNoPlayersHaveBeenOnlineScheduled = false;
			}

			if (nextRestartIfNoPlayersHaveBeenOnline <= now && isRestartIfNoPlayersHaveBeenOnlineScheduled) {
				// Restart server
				RestartCommand.execute(server.createCommandSourceStack());
				// Schedule the next restart, for when the restart doesn't stop the server (stopServer is disabled, or the restart script failed)
				nextRestartIfNoPlayersHaveBeenOnline = -1;
				isRestartIfNoPlayersHaveBeenOnlineScheduled = false;
			}
		} else {
			if (server.getPlayerCount() <= 0) {
				// Schedule no players restart
				nextRestartIfNoPlayersHaveBeenOnline = now + RestartServer.config.noPlayersWaitTime;
				isRestartIfNoPlayersHaveBeenOnlineScheduled = true;
			}
		}
	}
}
