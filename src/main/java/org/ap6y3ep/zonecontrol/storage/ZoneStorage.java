package org.ap6y3ep.zonecontrol.storage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import org.ap6y3ep.zonecontrol.util.Zone;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ZoneStorage extends PersistentState {
    private static final String DATA_NAME = "zonecontrol_zones";
    private final Map<String, Zone> zones = new HashMap<>();

    public static ZoneStorage get (ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        return manager.getOrCreate(
            nbt -> {
                ZoneStorage storage = new ZoneStorage();
                storage.readNbt(nbt);
                return storage;
            },
            ZoneStorage::new,
            DATA_NAME
        );
    }

    public boolean isInZone (Vec3d playerPos) {
        return zones.values().stream().anyMatch(box -> box.contains(playerPos));
    }

    public Zone getZone (String name) {
        return zones.get(name);
    }

    public boolean canEnter (PlayerEntity player) {
        Optional<Zone> optinal = zones.values().stream().filter(box -> box.contains(player.getPos())).findFirst();
        if (optinal.isPresent()) {
            return optinal.get().canEnter(player.getName().getString());
        }
        return false;
    }

    public boolean addZone (String name, BlockPos pos1, BlockPos pos2, String owner) {
        if (!zones.containsKey(name)) {
            zones.put(name, Zone.of(name, pos1, pos2, owner));
            return true;
        }
        return false;
    }

    public Zone removeZone (String name) {
        return zones.remove(name);
    }

    public Map<String, Zone> getZones() {
        return zones;
    }

    public void readNbt (NbtCompound nbt) {
        zones.clear();
        NbtList nbtList = nbt.getList("zones", 10);
        System.out.println("IS CONTAINS " + String.valueOf(nbt.contains("zones", 9)));
        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound compound = nbtList.getCompound(i);
            String name = compound.getString("name");
            Zone zone = Zone.fromNbt(compound.getCompound("zone"));
            zones.put(name, zone);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        zones.forEach((name, box) -> {
            NbtCompound compound = new NbtCompound();
            compound.putString("name", name);
            compound.put("zone", box.toNbt());
            nbtList.add(compound);
        });
        nbt.put("zones", nbtList);
        return nbt;
    }
}
