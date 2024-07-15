package org.ap6y3ep.zonecontrol.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class Zone {
    private String name;
    private Box zone;

    private Set<String> owners = new HashSet<>();
    private Set<String> allowedPlayers = new HashSet<>();

    public static Zone of (String name, BlockPos pos1, BlockPos pos2, String owner) {
        return new Zone (name, new Box(pos1, pos2), owner);
    }

    public Zone (String name, Box zone, String owner) {
        this.name = name;
        this.zone = zone;
        this.owners.add(owner);
    }

    public Zone (String name, Box zone, Set<String> owners, Set<String> allowedPlayers) {
        this.name = name;
        this.zone = zone;
        this.owners = owners;
        this.allowedPlayers = allowedPlayers;
    }

    public NbtCompound toNbt () {
        NbtCompound nbt = new NbtCompound();
        nbt.putString("name", name);
        nbt.putLong("pos1", minBlockPos().asLong());
        nbt.putLong("pos2", maxBlockPos().asLong());

        NbtList owners = new NbtList();
        this.owners.forEach((owner) -> {
            NbtCompound compound = new NbtCompound();
            compound.putString("name", owner);
            owners.add(compound);
        });
        nbt.put("owners", owners);

        NbtList allowedPlayers = new NbtList();
        this.allowedPlayers.forEach((playerName) -> {
            NbtCompound compound = new NbtCompound();
            compound.putString("name", playerName);
            allowedPlayers.add(compound);
        });
        nbt.put("allowedPlayers", allowedPlayers);

        return nbt;
    }

    public static Zone fromNbt (NbtCompound nbt) {
        String name = nbt.getString("name");
        BlockPos pos1 = BlockPos.fromLong(nbt.getLong("pos1"));
        BlockPos pos2 = BlockPos.fromLong(nbt.getLong("pos2"));

        Set<String> owners = new HashSet<>();
        NbtList nbtOwners = nbt.getList("owners", 10);
        for (int i = 0; i < nbtOwners.size(); i++) {
            owners.add(nbtOwners.getCompound(i).getString("name"));
        }

        boolean allowedAll = nbt.getBoolean("allowedAll");

        Set<String> allowedPlayers = new HashSet<>();
        NbtList nbtAllowedPlayers = nbt.getList("allowedPlayers", 10);
        for (int i = 0; i < nbtAllowedPlayers.size(); i++) {
            allowedPlayers.add(nbtAllowedPlayers.getCompound(i).getString("name"));
        }

        return new Zone (name, new Box (pos1, pos2), owners, allowedPlayers);
    }

    public boolean canEnter (String playerName) {
        return owners.contains(playerName) || allowedPlayers.contains(playerName);
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Box getZone() {
        return zone;
    }

    public void setZone (Box zone) {
        this.zone = zone;
    }

    public Set<String> getOwners() {
        return owners;
    }

    public void addOwners (Collection<ServerPlayerEntity> players) {
        players.forEach(player -> owners.add(player.getName().getString()));
    }

    public void removeOwners (Collection<ServerPlayerEntity> players) {
        players.forEach(player -> owners.remove(player.getName().getString()));
    }

    public Set<String> getAllowedPlayers() {
        return allowedPlayers;
    }

    public void addAllowedPlayers (Collection<ServerPlayerEntity> players) {
        players.forEach(player -> allowedPlayers.add(player.getName().getString()));
    }

    public void removeAllowedPlayers (Collection<ServerPlayerEntity> players) {
        players.forEach(player -> allowedPlayers.remove(player.getName().getString()));
    }

    public BlockPos minBlockPos () {
        return new BlockPos((int) zone.minX, (int) zone.minY, (int) zone.minZ);
    }

    public BlockPos maxBlockPos () {
        return new BlockPos((int) zone.maxX, (int) zone.maxY, (int) zone.maxZ);
    }

    public boolean contains (Vec3d pos) {
        return zone.minX <= pos.x && pos.x <= zone.maxX + 1
                && zone.minY <= pos.y && pos.y <= zone.maxY + 1
                && zone.minZ <= pos.z && pos.z <= zone.maxZ + 1;
    }

    public String shortInfo () {
        return String.format("\"%s\": (%d %d %d), (%d %d %d)", name,
                (int) zone.minX, (int) zone.minY, (int) zone.minZ,
                (int) zone.maxX, (int) zone.maxY, (int) zone.maxZ);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Name: \"").append(name).append("\"");

        sb.append(String.format("\nCoordinates: (%d %d %d), (%d %d %d)",
                (int) zone.minX, (int) zone.minY, (int) zone.minZ,
                (int) zone.maxX, (int) zone.maxY, (int) zone.maxZ));

        sb.append("\nOwners: ");
        owners.forEach(owner -> sb.append(owner).append(", "));

        sb.append("\nAllowed players: ");
        allowedPlayers.forEach(player -> sb.append(player).append(", "));

        return sb.toString();
    }
}
