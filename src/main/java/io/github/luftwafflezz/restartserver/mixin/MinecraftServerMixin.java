package io.github.luftwafflezz.restartserver.mixin;

import io.github.luftwafflezz.restartserver.RestartServer;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
	// Minecraft pauses an empty server (server.properties: pause-when-empty-seconds, 60 by default) by returning early from tickServer.
	// That early return skips the tick events of Fabric API, so the scheduler is ticked from the head of tickServer instead.
	@Inject(method = "tickServer", at = @At("HEAD"))
	private void restartserver$onTickServer(BooleanSupplier haveTime, CallbackInfo ci) {
		RestartServer.SCHEDULER.onTick((MinecraftServer) (Object) this);
	}
}
