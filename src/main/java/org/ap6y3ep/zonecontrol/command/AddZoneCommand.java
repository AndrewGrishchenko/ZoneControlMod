package org.ap6y3ep.zonecontrol.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.ap6y3ep.zonecontrol.storage.ZoneStorage;

public class AddZoneCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher,
                                CommandRegistryAccess commandRegistryAccess,
                                CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("addZone")
                .then(CommandManager.argument("name", StringArgumentType.string())
                .then(CommandManager.argument("pos1", BlockPosArgumentType.blockPos())
                .then(CommandManager.argument("pos2", BlockPosArgumentType.blockPos())
                .executes(context -> {
                    String name = StringArgumentType.getString(context, "name");
                    BlockPos pos1 = BlockPosArgumentType.getBlockPos(context, "pos1");
                    BlockPos pos2 = BlockPosArgumentType.getBlockPos(context, "pos2");

                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();
                    if (player != null) {
                        ZoneStorage storage = ZoneStorage.get((ServerWorld) player.getWorld());
                        if (storage.addZone(name, pos1, pos2)) {
                            storage.markDirty();
                            player.sendMessage(Text.of("Zone " + name + " added!"));
                        } else {
                            player.sendMessage(Text.of("Zone with name \"" + name + "\" already exists!"));
                        }
                    }
                    return 1;
                })))));
    }
}
