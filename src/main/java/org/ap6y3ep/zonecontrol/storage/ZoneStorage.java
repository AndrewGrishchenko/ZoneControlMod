package org.ap6y3ep.zonecontrol.storage;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.Map;

public class ZoneStorage extends PersistentState {
    private static final String DATA_NAME = "zonecontrol_zones";
    private final Map<String, Box> zones = new HashMap<>();

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

    public boolean addZone (String name, BlockPos pos1, BlockPos pos2) {
        if (!zones.containsKey(name)) {
            zones.put(name, new Box(pos1, pos2));
            return true;
        }
        return false;
    }

    public Box removeZone (String name) {
        return zones.remove(name);
    }

    public Map<String, Box> getZones() {
        return zones;
    }

    public void readNbt (NbtCompound nbt) {
        zones.clear();
        NbtList nbtList = nbt.getList("zones", 10);
        for (int i = 0; i < nbtList.size(); i++) {
            NbtCompound compound = nbtList.getCompound(i);
            String name = compound.getString("name");
            BlockPos pos1 = BlockPos.fromLong(compound.getLong("pos1"));
            BlockPos pos2 = BlockPos.fromLong(compound.getLong("pos2"));
            zones.put(name, new Box(pos1, pos2));
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList nbtList = new NbtList();
        zones.forEach((name, box) -> {
            NbtCompound compound = new NbtCompound();
            compound.putString("name", name);
            compound.putLong("pos1", new BlockPos((int) box.minX, (int) box.minY, (int) box.minZ).asLong());
            compound.putLong("pos2", new BlockPos((int) box.maxX, (int) box.maxY, (int) box.maxZ).asLong());
            nbtList.add(compound);
        });
        nbt.put("zones", nbtList);
        return nbt;
    }
}
