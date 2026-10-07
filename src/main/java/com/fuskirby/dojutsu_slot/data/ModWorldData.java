package com.fuskirby.dojutsu_slot.data;

import com.fuskirby.dojutsu_slot.enums.WorldMode;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.WorldServer;

public class ModWorldData extends WorldSavedData {
    private static final String DATA_NAME = "dojutsu_slot_world_mode";
    private WorldMode mode = WorldMode.CLASSIC;
    private boolean modeSet = false;

    public ModWorldData() {
        super(DATA_NAME);
    }

    public ModWorldData(String name) {
        super(name);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        if (nbt.hasKey("DojutsuMode")) {
            mode = WorldMode.valueOf(nbt.getString("DojutsuMode"));
            modeSet = nbt.getBoolean("ModeSet");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setString("DojutsuMode", mode.name());
        compound.setBoolean("ModeSet", modeSet);
        return compound;
    }

    public WorldMode getMode() {
        return mode;
    }

    public boolean isModeSet() {
        return modeSet;
    }

    public void setMode(WorldMode mode) {
        this.mode = mode;
        this.modeSet = true;
        markDirty();
    }

    public static ModWorldData get(World world) {
        if (world.isRemote) return null;
        WorldServer worldServer = (WorldServer) world;
        MapStorage storage = worldServer.getMapStorage();
        ModWorldData instance = null;
        if (storage != null) {
            instance = (ModWorldData) storage.getOrLoadData(ModWorldData.class, DATA_NAME);
        }
        if (instance == null) {
            instance = new ModWorldData();
            if (storage != null) {
                storage.setData(DATA_NAME, instance);
            }
        }
        return instance;
    }
}