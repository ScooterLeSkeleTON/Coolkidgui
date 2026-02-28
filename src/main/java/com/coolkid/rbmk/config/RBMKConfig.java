package com.coolkid.rbmk.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class RBMKConfig {
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue BASE_HEAT_GAIN;
    public static final ForgeConfigSpec.DoubleValue PASSIVE_COOLING;
    public static final ForgeConfigSpec.DoubleValue MELTDOWN_HEAT;
    public static final ForgeConfigSpec.DoubleValue STEAM_PRESSURE_LIMIT;
    public static final ForgeConfigSpec.IntValue RADIATION_TICK_INTERVAL;
    public static final ForgeConfigSpec.DoubleValue FALLOUT_RADIUS;
    public static final ForgeConfigSpec.BooleanValue ENABLE_GRAPHITE_FIRE;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("reactor");
        BASE_HEAT_GAIN = builder.comment("Heat generated each tick at base flux")
                .defineInRange("baseHeatGain", 1.6D, 0.1D, 100D);
        PASSIVE_COOLING = builder.comment("Natural passive cooling each tick")
                .defineInRange("passiveCooling", 0.72D, 0.0D, 25D);
        MELTDOWN_HEAT = builder.comment("Heat threshold to trigger catastrophic event")
                .defineInRange("meltdownHeat", 1400D, 200D, 7000D);
        STEAM_PRESSURE_LIMIT = builder.comment("Pressure threshold for channel rupture")
                .defineInRange("steamPressureLimit", 320D, 20D, 3000D);
        ENABLE_GRAPHITE_FIRE = builder.comment("Whether disasters may ignite persistent graphite fires")
                .define("enableGraphiteFire", true);
        builder.pop();

        builder.push("radiation");
        RADIATION_TICK_INTERVAL = builder.comment("How often radiation effects are applied")
                .defineInRange("radiationTickInterval", 20, 1, 200);
        FALLOUT_RADIUS = builder.comment("Radius around a disaster where fallout remains dangerous")
                .defineInRange("falloutRadius", 220D, 10D, 2048D);
        builder.pop();
        SPEC = builder.build();
    }

    private RBMKConfig() {
    }
}
