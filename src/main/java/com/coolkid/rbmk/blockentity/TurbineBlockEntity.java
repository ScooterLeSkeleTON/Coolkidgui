package com.coolkid.rbmk.blockentity;

import com.coolkid.rbmk.init.RBMKBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TurbineBlockEntity extends BlockEntity {
    private double steamInput;
    private double rotorRpm;
    private double generatedPower;

    public TurbineBlockEntity(BlockPos pos, BlockState state) {
        super(RBMKBlockEntities.STEAM_TURBINE.get(), pos, state);
    }

    public void tickServer() {
        rotorRpm = Mth.clamp(rotorRpm + steamInput * 22D - 18D, 0D, 7200D);
        generatedPower = rotorRpm * 0.18D;
        steamInput = Math.max(0, steamInput * 0.55D - 0.01D);
        setChanged();
    }

    public void addSteam(double steam) {
        steamInput = Mth.clamp(steamInput + steam, 0D, 120D);
    }

    public double getGeneratedPower() {
        return generatedPower;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        steamInput = tag.getDouble("SteamInput");
        rotorRpm = tag.getDouble("RotorRpm");
        generatedPower = tag.getDouble("GeneratedPower");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("SteamInput", steamInput);
        tag.putDouble("RotorRpm", rotorRpm);
        tag.putDouble("GeneratedPower", generatedPower);
    }
}
