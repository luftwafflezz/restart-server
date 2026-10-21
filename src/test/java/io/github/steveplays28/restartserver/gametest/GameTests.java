package io.github.steveplays28.restartserver.gametest;

import io.github.steveplays28.restartserver.RestartScheduler;
import io.github.steveplays28.restartserver.RestartServer;
import io.github.steveplays28.restartserver.commands.RestartCommand;
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
		});

		var ownerSource = new CommandSourceStack(CommandSource.NULL, Vec3.ZERO, Vec2.ZERO, level, LevelBasedPermissionSet.OWNER, Component.literal("owner"), server);
		helper.assertTrue(requirement.test(ownerSource), "/restart must be allowed for permission level: owner");
		helper.assertTrue(requirement.test(server.createCommandSourceStack()), "/restart must be allowed for the server console");

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

			var scheduler = new RestartScheduler();
			scheduler.onTick(server);
			helper.assertTrue(scheduler.isRestartScheduled, "The interval restart should have been scheduled");
			helper.assertTrue(scheduler.isRestartIfNoPlayersHaveBeenOnlineScheduled, "The restart for when no players have been online should have been scheduled");

			// Both restarts are due, the scheduler restarts through the command (which doesn't stop the server in this test)
			var due = Instant.now().getEpochSecond() - 1;
			scheduler.nextRestart = due;
			scheduler.nextRestartIfNoPlayersHaveBeenOnline = due;
			scheduler.onTick(server);

			helper.succeed();
		} finally {
			config.restartInterval = restartInterval;
			config.restartIfNoPlayersHaveBeenOnline = restartIfNoPlayersHaveBeenOnline;
			config.noPlayersWaitTime = noPlayersWaitTime;
			config.stopServer = stopServer;
			config.runRestartScript = runRestartScript;
		}
	}
}
