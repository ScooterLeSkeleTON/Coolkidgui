package com.coolkid.rbmk.radiation;

import com.coolkid.rbmk.config.RBMKConfig;
import com.coolkid.rbmk.init.RBMKEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class RadiationManager {
    private static final List<FalloutZone> FALLOUT_ZONES = new ArrayList<>();
    private static final Map<Long, Double> CHUNK_CONTAMINATION = new HashMap<>();
    private static final Map<UUID, Double> CUMULATIVE_DOSE = new HashMap<>();

    public static void addFallout(ServerLevel level, BlockPos center, double intensity, boolean severe) {
        FALLOUT_ZONES.add(new FalloutZone(level.dimension().location().toString(), Vec3.atCenterOf(center), intensity, level.getGameTime(), severe));
        long key = chunkKey(center);
        CHUNK_CONTAMINATION.merge(key, intensity * (severe ? 0.5D : 0.2D), Double::sum);
    }

    public static void tickPlayers(ServerLevel level) {
        long gameTime = level.getGameTime();
        if (gameTime % RBMKConfig.RADIATION_TICK_INTERVAL.get() != 0) {
            return;
        }

        FALLOUT_ZONES.removeIf(zone -> !zone.dimensionId.equals(level.dimension().location().toString()) || gameTime - zone.createdAt > 20L * 60L * 45L);
        CHUNK_CONTAMINATION.replaceAll((k, v) -> Math.max(0D, v - 0.0045D));
        CHUNK_CONTAMINATION.values().removeIf(v -> v <= 0.001D);

        for (ServerPlayer player : level.players()) {
            double localDose = computeDoseForPosition(level, player.position()) + chunkDose(player.blockPosition());
            double totalDose = CUMULATIVE_DOSE.merge(player.getUUID(), localDose * 0.03D, Double::sum);
            CUMULATIVE_DOSE.computeIfPresent(player.getUUID(), (k, v) -> Math.max(0D, v - 0.0008D));

            if (localDose <= 0.06D && totalDose < 0.4D) {
                continue;
            }

            int baseAmp = localDose > 7.0D ? 3 : localDose > 3.8D ? 2 : localDose > 1.2D ? 1 : 0;
            int longTermAmp = totalDose > 12D ? 2 : totalDose > 4D ? 1 : 0;

            player.addEffect(new MobEffectInstance(RBMKEffects.RADIATION_SICKNESS.get(), (int) (80 + localDose * 38), baseAmp, true, true));

            if (localDose > 2.0D || totalDose > 5.0D) {
                player.addEffect(new MobEffectInstance(RBMKEffects.RADIATION_BURN.get(), (int) (50 + localDose * 22), Math.max(0, baseAmp - 1), true, true));
            }

            if (totalDose > 9.0D) {
                player.addEffect(new MobEffectInstance(RBMKEffects.ACUTE_RADIATION_SYNDROME.get(), (int) (120 + totalDose * 8), longTermAmp, true, true));
            }
        }
    }

    public static double computeDoseForPosition(ServerLevel level, Vec3 pos) {
        double total = 0;
        for (FalloutZone zone : FALLOUT_ZONES) {
            if (!zone.dimensionId.equals(level.dimension().location().toString())) {
                continue;
            }

            double distance = zone.center.distanceTo(pos);
            double range = RBMKConfig.FALLOUT_RADIUS.get() * (zone.severe ? 1.4D : 1.0D);
            if (distance > range) {
                continue;
            }

            double distanceFalloff = Math.exp(-distance / (zone.severe ? 32D : 46D));
            total += zone.intensity * distanceFalloff;
        }

        return total;
    }

    public static double getCumulativeDose(UUID playerId) {
        return CUMULATIVE_DOSE.getOrDefault(playerId, 0D);
    }

    private static double chunkDose(BlockPos pos) {
        return CHUNK_CONTAMINATION.getOrDefault(chunkKey(pos), 0D) * 0.3D;
    }

    private static long chunkKey(BlockPos pos) {
        long x = pos.getX() >> 4;
        long z = pos.getZ() >> 4;
        return (x & 0xffffffffL) | (z << 32);
    }

    private record FalloutZone(String dimensionId, Vec3 center, double intensity, long createdAt, boolean severe) {
    }

    private RadiationManager() {
    }
}
