package org.ap6y3ep.zonecontrol.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.ap6y3ep.zonecontrol.storage.ZoneStorage;
import org.ap6y3ep.zonecontrol.util.Zone;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class ZoneCommand {
    public static void register (CommandDispatcher<ServerCommandSource> dispatcher,
                                 CommandRegistryAccess commandRegistryAccess,
                                 CommandManager.RegistrationEnvironment registrationEnvironment) {
        dispatcher.register(CommandManager.literal("zone")
                .then(CommandManager.literal("add")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                        .then(CommandManager.argument("pos1", BlockPosArgumentType.blockPos())
                        .then(CommandManager.argument("pos2", BlockPosArgumentType.blockPos())
                        .executes(ZoneCommand::executeAdd)))))
                .then(CommandManager.literal("remove")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                        .executes(ZoneCommand::executeRemove)))
                .then(CommandManager.literal("list")
                        .executes(ZoneCommand::executeList))
                .then(CommandManager.literal("allow")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                        .then(CommandManager.argument("player", EntityArgumentType.players())
                        .executes(ZoneCommand::executeAllow))))
                .then(CommandManager.literal("disallow")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                        .then(CommandManager.argument("player", EntityArgumentType.players())
                        .executes(ZoneCommand::executeDisallow))))
                .then(CommandManager.literal("info")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                        .executes(ZoneCommand::executeInfo)))
                .then(CommandManager.literal("addOwner")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                        .then(CommandManager.argument("player", EntityArgumentType.players())
                        .executes(ZoneCommand::executeAddOwner))))
                .then(CommandManager.literal("removeOwner")
                        .then(CommandManager.argument("name", StringArgumentType.string())
                        .then(CommandManager.argument("player", EntityArgumentType.players())
                        .executes(ZoneCommand::executeRemoveOwner)))));
    }

    private static int executeAdd (CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        BlockPos pos1 = BlockPosArgumentType.getBlockPos(context, "pos1");
        BlockPos pos2 = BlockPosArgumentType.getBlockPos(context, "pos2");

        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player != null) {
            ZoneStorage storage = ZoneStorage.get((ServerWorld) player.getWorld());
            if (storage.addZone(name, pos1, pos2, player.getName().getString())) {
                storage.markDirty();
                player.sendMessage(Text.of("Zone " + name + " added!"));
            } else {
                player.sendMessage(Text.of("Zone with name \"" + name + "\" already exists!"));
            }
        }
        return 1;
    }

    private static int executeRemove (CommandContext<ServerCommandSource> context) {
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
    }

    private static int executeList (CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (player != null) {
            StringBuilder sb = new StringBuilder();
            ZoneStorage storage = ZoneStorage.get((ServerWorld) player.getWorld());
            storage.getZones().forEach((name, box) -> {
                sb.append(box.shortInfo());
                sb.append("\n");
            });
            if (!sb.isEmpty()) {
                sb.setLength(sb.length() - 1);
                player.sendMessage(Text.of(sb.toString()));
            } else {
                player.sendMessage(Text.of("No zones in this world!"));
            }
        }
        return 1;
    }

    private static int executeAllow (CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        Collection<ServerPlayerEntity> playerTarget = null;

        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            try {
                playerTarget = EntityArgumentType.getPlayers(context, "player");
            } catch (CommandSyntaxException e) {
                player.sendMessage(Text.of("Is not a player!"));
                return 1;
            }

            ZoneStorage storage = ZoneStorage.get((ServerWorld) player.getWorld());
            Zone zone = storage.getZone(name);
            if (zone == null) {
                player.sendMessage(Text.of("Zone \"" + name + "\" does not exist!"));
            } else if (!zone.getOwners().contains(player.getName().getString())) {
                player.sendMessage(Text.of("Only owners can modify zone!"));
            } else {
                zone.addAllowedPlayers(playerTarget);
                player.sendMessage(Text.of("Allowed players to zone!"));
            }
        }
        return 1;
    }

    private static int executeDisallow (CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        Collection<ServerPlayerEntity> playerTarget = null;

        ServerCommandSource source = context.getSource();
        PlayerEntity player = source.getPlayer();

        if (player != null) {
            try {
                playerTarget = EntityArgumentType.getPlayers(context, "player");
            } catch (CommandSyntaxException e) {
                player.sendMessage(Text.of("Is not a player!"));
                return 1;
            }

            ZoneStorage storage = ZoneStorage.get((ServerWorld) player.getWorld());
            Zone zone = storage.getZone(name);
            if (zone == null) {
                player.sendMessage(Text.of("Zone \"" + name + "\" does not exist!"));
            } else if (!zone.getOwners().contains(player.getName().getString())) {
                player.sendMessage(Text.of("Only owners can modify zone!"));
            } else {
                zone.removeAllowedPlayers(playerTarget);
                player.sendMessage(Text.of("Disallowed players from zone!"));
            }
        }
        return 1;
    }

    private static int executeInfo (CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");

        ServerCommandSource source = context.getSource();
        PlayerEntity player = source.getPlayer();

        if (player != null) {
            ZoneStorage storage = ZoneStorage.get((ServerWorld) player.getWorld());
            Zone zone = storage.getZone(name);
            if (zone == null) {
                player.sendMessage(Text.of("Zone \"" + name + "\" does not exist!"));
            } else {
                player.sendMessage(Text.of(zone.toString()));
            }
        }
        return 1;
    }

    private static int executeAddOwner (CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        Collection<ServerPlayerEntity> playerTarget = null;

        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            try {
                playerTarget = EntityArgumentType.getPlayers(context, "player");
            } catch (CommandSyntaxException e) {
                player.sendMessage(Text.of("Is not a player!"));
                return 1;
            }

            ZoneStorage storage = ZoneStorage.get((ServerWorld) player.getWorld());
            Zone zone = storage.getZone(name);
            if (zone == null) {
                player.sendMessage(Text.of("Zone \"" + name + "\" does not exist!"));
            } else if (!zone.getOwners().contains(player.getName().getString())) {
                player.sendMessage(Text.of("Only owners can modify zone!"));
            } else {
                zone.addOwners(playerTarget);
                player.sendMessage(Text.of("Added players to zone owners!"));
            }
        }
        return 1;
    }

    private static int executeRemoveOwner (CommandContext<ServerCommandSource> context) {
        String name = StringArgumentType.getString(context, "name");
        Collection<ServerPlayerEntity> playerTarget = null;

        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();

        if (player != null) {
            try {
                playerTarget = EntityArgumentType.getPlayers(context, "player");
            } catch (CommandSyntaxException e) {
                player.sendMessage(Text.of("Is not a player!"));
                return 1;
            }

            ZoneStorage storage = ZoneStorage.get((ServerWorld) player.getWorld());
            Zone zone = storage.getZone(name);
            if (zone == null) {
                player.sendMessage(Text.of("Zone \"" + name + "\" does not exist!"));
            } else if (!zone.getOwners().contains(player.getName().getString())) {
                player.sendMessage(Text.of("Only owners can modify zone!"));
            } else {
                zone.removeOwners(playerTarget);
                player.sendMessage(Text.of("Removed players from zone owners!"));
            }
        }
        return 1;
    }
}
