package org.ap6y3ep.zonecontrol.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import org.ap6y3ep.zonecontrol.PlayerEnterZoneCallback;
import org.ap6y3ep.zonecontrol.storage.ZoneStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    @Unique
    private Vec3d lastSafePosition;

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void onTickMovement(CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        Vec3d playerPos = player.getPos();

        if (player.getWorld().isClient()) return;

        ZoneStorage storage = ZoneStorage.get((ServerWorld) player.getWorld());

        if (storage.isInZone(playerPos)) {
            boolean canEnter = PlayerEnterZoneCallback.EVENT.invoker().onPlayerEnterZone(player);

            if (!canEnter) {
                if (lastSafePosition != null) {
                    player.teleport(lastSafePosition.x, lastSafePosition.y, lastSafePosition.z);
                    player.sendMessage(Text.literal("You cannot enter this area!"), true);
                }
                info.cancel();
            } else {
                lastSafePosition = playerPos;
            }
        } else {
            lastSafePosition = playerPos;
        }
    }
}
