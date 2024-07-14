package org.ap6y3ep.zonecontrol;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.player.PlayerEntity;
import org.ap6y3ep.zonecontrol.command.AddZoneCommand;
import org.ap6y3ep.zonecontrol.command.ListZonesCommand;
import org.ap6y3ep.zonecontrol.command.RemoveZoneCommand;

public class ZoneControl implements ModInitializer {
    @Override
    public void onInitialize() {
        System.out.println("Hello from Fabric mod!");
        PlayerEnterZoneCallback.EVENT.register(this::onPlayerEnterZone);
        registerCommands();
    }

    private boolean onPlayerEnterZone (PlayerEntity player) {
        return false;
    }

    private void registerCommands () {
        CommandRegistrationCallback.EVENT.register(AddZoneCommand::register);
        CommandRegistrationCallback.EVENT.register(ListZonesCommand::register);
        CommandRegistrationCallback.EVENT.register(RemoveZoneCommand::register);
    }
}
