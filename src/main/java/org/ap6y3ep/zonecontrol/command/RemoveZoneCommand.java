package org.ap6y3ep.zonecontrol.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.ap6y3ep.zonecontrol.storage.ZoneStorage;

public class RemoveZoneCommand {
    public static void register (CommandDispatcher<ServerCommandSource> dispatcher,
                                 CommandRegistryAccess commandRegistryAccess,
                                 CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("removeZone")
                .then(CommandManager.argument("name", StringArgumentType.string())
                .executes(context -> {
                    String name = StringArgumentType.getString(context, "name");

                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();
                    if (player != null) {
                        ZoneStorage storage = ZoneStorage.get((ServerWorld) player.getWorld());
                        if (storage.removeZone(name) != null) {
                            player.sendMessage(Text.of("Zone \"" + name + "\" has been deleted!"));
                        } else {
                            player.sendMessage(Text.of("No zone with name \"" + name + "\" in this world!"));
                        }
                    }
                    return 1;
                })));
    }
}
