package io.github.luftwafflezz.restartserver.gametest;

import io.github.luftwafflezz.restartserver.RestartScheduler;
import io.github.luftwafflezz.restartserver.RestartServer;
import io.github.luftwafflezz.restartserver.commands.RestartCommand;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.server.permissions.PermissionSet;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.time.Instant;
import java.util.Map;

public class GameTests {
	@GameTest
	public void testRestartCommand(GameTestHelper helper) {
		var level = helper.getLevel();
		var server = level.getServer();

		RestartServer.config.stopServer = false;
		RestartServer.config.runRestartScript = false;

		var source = new CommandSourceStack(CommandSource.NULL, new Vec3(0, 0, 0), new Vec2(0, 0), level, PermissionSet.NO_PERMISSIONS, server, new Zombie(level));
		RestartCommand.execute(source);

		helper.succeed();
	}

	@GameTest
	public void testRestartCommandRequiresOwnerPermission(GameTestHelper helper) {
		var level = helper.getLevel();
		var server = level.getServer();

		// The command has to be registered on the server's command dispatcher, and it has to use the same requirement as RestartCommand
		var node = server.getCommands().getDispatcher().getRoot().getChild(RestartCommand.NAME);
		helper.assertTrue(node != null, "/restart must be registered");
		var requirement = RestartCommand.register().getRequirement();

		// Same as the original op level 4 requirement: only owner level operators and the console may run /restart
		var deniedPermissions = Map.of(
				"no permissions", PermissionSet.NO_PERMISSIONS,
				"moderator", LevelBasedPermissionSet.MODERATOR,
				"gamemaster", LevelBasedPermissionSet.GAMEMASTER,
				"admin", LevelBasedPermissionSet.ADMIN
		);
		deniedPermissions.forEach((name, permissions) -> {
			var source = new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, level, permissions, Component.literal(name), server);
			helper.assertFalse(requirement.test(source), "/restart must be denied for permission level: " + name);
			helper.assertFalse(node.canUse(source), "The registered /restart command must be denied for permission level: " + name);
		});

		var ownerSource = new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, level, LevelBasedPermissionSet.OWNER, Component.literal("owner"), server);
		helper.assertTrue(requirement.test(ownerSource), "/restart must be allowed for permission level: owner");
		helper.assertTrue(node.canUse(ownerSource), "The registered /restart command must be allowed for permission level: owner");
		helper.assertTrue(node.canUse(server.createCommandSourceStack()), "The registered /restart command must be allowed for the server console");

		helper.succeed();
	}

	@GameTest
	public void testRestartSchedulerTick(GameTestHelper helper) {
		var server = helper.getLevel().getServer();
		var config = RestartServer.config;

		var restartInterval = config.restartInterval;
		var restartIfNoPlayersHaveBeenOnline = config.restartIfNoPlayersHaveBeenOnline;
		var noPlayersWaitTime = config.noPlayersWaitTime;
		var stopServer = config.stopServer;
		var runRestartScript = config.runRestartScript;

		try {
			config.restartInterval = 3600;
			config.restartIfNoPlayersHaveBeenOnline = true;
			config.noPlayersWaitTime = 3600;
			config.stopServer = false;
			config.runRestartScript = false;

			helper.assertTrue(server.getPlayerCount() == 0, "The test server shouldn't have any players");

			var scheduler = new RestartScheduler();
			scheduler.onTick(server);
			helper.assertTrue(scheduler.isRestartScheduled, "The interval restart should have been scheduled");
			helper.assertTrue(scheduler.isRestartIfNoPlayersHaveBeenOnlineScheduled, "The restart for when no players have been online should have been scheduled");

			// The interval restart is due: the scheduler restarts through the command, and the server isn't stopped in this test, so the next restart has to be scheduled again
			var due = Instant.now().getEpochSecond() - 1;
			scheduler.nextRestart = due;
			scheduler.onTick(server);
			helper.assertFalse(scheduler.isRestartScheduled, "The interval restart should have been executed, and left unscheduled until the next tick");
			helper.assertTrue(scheduler.isRestartIfNoPlayersHaveBeenOnlineScheduled, "The restart for when no players have been online isn't due yet");
			scheduler.onTick(server);
			helper.assertTrue(scheduler.isRestartScheduled, "The next interval restart should have been scheduled");

			// The restart for when no players have been online is due
			scheduler.nextRestartIfNoPlayersHaveBeenOnline = due;
			scheduler.onTick(server);
			helper.assertFalse(scheduler.isRestartIfNoPlayersHaveBeenOnlineScheduled, "The restart for when no players have been online should have been executed, and left unscheduled until the next tick");
			helper.assertTrue(scheduler.isRestartScheduled, "The interval restart isn't due yet");

			helper.succeed();
		} finally {
			config.restartInterval = restartInterval;
			config.restartIfNoPlayersHaveBeenOnline = restartIfNoPlayersHaveBeenOnline;
			config.noPlayersWaitTime = noPlayersWaitTime;
			config.stopServer = stopServer;
			config.runRestartScript = runRestartScript;
		}
	}

	@GameTest
	public void testRestartSchedulerIsTickedByTheServer(GameTestHelper helper) {
		var config = RestartServer.config;
		var restartInterval = config.restartInterval;

		config.restartInterval = 3600;
		RestartServer.SCHEDULER.isRestartScheduled = false;

		// MinecraftServerMixin ticks the scheduler every server tick, which schedules the restart
		helper.succeedWhen(() -> {
			helper.assertTrue(RestartServer.SCHEDULER.isRestartScheduled, "The server should have ticked the restart scheduler");

			config.restartInterval = restartInterval;
			RestartServer.SCHEDULER.isRestartScheduled = false;
		});
	}
}
