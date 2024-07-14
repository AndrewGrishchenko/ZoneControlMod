package org.ap6y3ep.zonecontrol;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class ZoneControl implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("Hello from Fabric mod!");
        PlayerEnterZoneCallback.EVENT.register(this::onPlayerEnterZone);
    }

    private boolean onPlayerEnterZone (PlayerEntity player) {
        boolean allowEntry = false;
        if (!allowEntry) {
            player.sendMessage(Text.literal("Access denied"), true);
        }
        return allowEntry;
    }
}
