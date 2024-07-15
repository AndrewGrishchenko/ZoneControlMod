package org.ap6y3ep.zonecontrol;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.ap6y3ep.zonecontrol.storage.ZoneStorage;

public interface PlayerEnterZoneCallback {
    Event<PlayerEnterZoneCallback> EVENT = EventFactory.createArrayBacked(PlayerEnterZoneCallback.class,
            (listeners) -> (player) -> {
                for (PlayerEnterZoneCallback listener : listeners) {
                    if (!listener.onPlayerEnterZone(player)) {
                        ZoneStorage storage = ZoneStorage.get((ServerWorld) player.getWorld());
                        return storage.canEnter(player);
                    }
                }
                return true;
            });

    boolean onPlayerEnterZone (PlayerEntity player);
}
