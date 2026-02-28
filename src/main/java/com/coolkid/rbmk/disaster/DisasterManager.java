package com.coolkid.rbmk.disaster;

import com.coolkid.rbmk.config.RBMKConfig;
import com.coolkid.rbmk.radiation.RadiationManager;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.block.Blocks;

public final class DisasterManager {
    public static void triggerRupture(ServerLevel level, BlockPos pos, float power, double falloutIntensity) {
        level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 3.5F, 0.6F);
        level.explode(null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
                power,
                true,
                Explosion.BlockInteraction.DESTROY_WITH_DECAY);

        RadiationManager.addFallout(level, pos, falloutIntensity, true);
    }

    public static void triggerCatastrophicMeltdown(ServerLevel level, BlockPos pos, double heat, double pressure, double graphiteFire) {
        float blastPower = (float) Math.min(40F, 12F + (float) (heat / 95D) + (float) (pressure / 180D));
        triggerRupture(level, pos, blastPower, Math.min(18D, 4D + heat / 120D + graphiteFire * 3D));

        for (int i = 0; i < 16; i++) {
            BlockPos secondary = pos.offset(level.random.nextInt(44) - 22, level.random.nextInt(14) - 4, level.random.nextInt(44) - 22);
            level.explode(null, secondary.getX(), secondary.getY(), secondary.getZ(), 4F + level.random.nextFloat() * 7F, i % 3 == 0, Explosion.BlockInteraction.DESTROY);
            if (RBMKConfig.ENABLE_GRAPHITE_FIRE.get() && level.random.nextFloat() < 0.45F) {
                level.setBlockAndUpdate(secondary, Blocks.FIRE.defaultBlockState());
            }
            RadiationManager.addFallout(level, secondary, 0.9D + level.random.nextDouble() * 2.5D, true);
        }
    }

    private DisasterManager() {
    }
}
