package org.ap6y3ep.zonecontrol.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.ap6y3ep.zonecontrol.storage.ZoneStorage;

public class ListZonesCommand {
    public static void register (CommandDispatcher<ServerCommandSource> dispatcher,
                                 CommandRegistryAccess commandRegistryAccess,
                                 CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("listZones")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();
                    if (player != null) {
                        StringBuilder sb = new StringBuilder();
                        ZoneStorage storage = ZoneStorage.get((ServerWorld) player.getWorld());
                        storage.getZones().forEach((name, box) -> {
                            sb.append(String.format("\"%s\": (%d %d %d), (%d %d %d)\n", name,
                                    (int) box.minX, (int) box.minY, (int) box.minZ,
                                    (int) box.maxX, (int) box.maxY, (int) box.maxZ));
                        });
                        if (!sb.isEmpty()) {
                            sb.setLength(sb.length() - 1);
                            player.sendMessage(Text.of(sb.toString()));
                        } else {
                            player.sendMessage(Text.of("No zones in this world!"));
                        }
                    }
                    return 1;
                }));
    }
}
