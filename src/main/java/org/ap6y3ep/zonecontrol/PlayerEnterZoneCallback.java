package org.ap6y3ep.zonecontrol;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;

public interface PlayerEnterZoneCallback {
    Event<PlayerEnterZoneCallback> EVENT = EventFactory.createArrayBacked(PlayerEnterZoneCallback.class,
            (listeners) -> (player) -> {
                for (PlayerEnterZoneCallback listener : listeners) {
                    if (!listener.onPlayerEnterZone(player)) {
                        return false;
                    }
                }
                return true;
            });

    boolean onPlayerEnterZone (PlayerEntity player);
}
