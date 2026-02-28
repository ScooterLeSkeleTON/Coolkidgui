package com.coolkid.rbmk.blockentity;

import com.coolkid.rbmk.init.RBMKBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WaterPumpBlockEntity extends BlockEntity {
    private double pumpRate;

    public WaterPumpBlockEntity(BlockPos pos, BlockState state) {
        super(RBMKBlockEntities.WATER_PUMP.get(), pos, state);
    }

    public void tickServer() {
        int sources = countNearbyWaterSources();
        pumpRate = Mth.clamp(sources * 0.24D, 0D, 2.5D);
        setChanged();
    }

    public double getPumpRate() {
        return pumpRate;
    }

    private int countNearbyWaterSources() {
        int count = 0;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -2; z <= 2; z++) {
                    cursor.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                    if (level != null && level.getFluidState(cursor).is(FluidTags.WATER)) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        pumpRate = tag.getDouble("PumpRate");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("PumpRate", pumpRate);
    }
}
