package org.ap6y3ep.zonecontrol.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.ap6y3ep.zonecontrol.PlayerEnterZoneCallback;
import org.ap6y3ep.zonecontrol.ZoneControl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin {
    private Vec3d lastSafePosition;

    @Inject(method = "tickMovement", at = @At("HEAD"), cancellable = true)
    private void onTickMovement(CallbackInfo info) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        Vec3d playerPos = player.getPos();
        Box restrictedArea = new Box(new Vec3d(100, 64, 100), new Vec3d(200, 128, 200));

        if (restrictedArea.contains(playerPos)) {
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
