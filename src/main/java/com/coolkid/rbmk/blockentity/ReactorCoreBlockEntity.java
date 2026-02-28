package com.coolkid.rbmk.blockentity;

import com.coolkid.rbmk.config.RBMKConfig;
import com.coolkid.rbmk.disaster.DisasterManager;
import com.coolkid.rbmk.init.RBMKBlockEntities;
import com.coolkid.rbmk.init.RBMKBlocks;
import com.coolkid.rbmk.radiation.RadiationManager;
import com.coolkid.rbmk.reactor.ReactorOperationalMode;
import com.coolkid.rbmk.reactor.ReactorTelemetry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ReactorCoreBlockEntity extends BlockEntity {
    private double controlRodInsertion = 0.38D;
    private double coolantFlow = 0.78D;

    private double neutronFlux = 0.9D;
    private double heat = 340D;
    private double steamPressure = 75D;
    private double xenonPoisoning = 0.07D;
    private double iodinePit = 0.12D;

    private double fuelBurnup = 0.02D;
    private double structuralIntegrity = 1.0D;
    private double containmentDamage;
    private double graphiteFire;
    private double steamOutput;

    private int fuelChannels;
    private int controlColumns;
    private int pumpCount;
    private int turbineCount;

    private boolean scrammed;
    private ReactorOperationalMode mode = ReactorOperationalMode.STARTUP;

    public ReactorCoreBlockEntity(BlockPos pos, BlockState state) {
        super(RBMKBlockEntities.REACTOR_CORE.get(), pos, state);
    }

    public void tickServer() {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }

        scanInfrastructure();
        simulateCorePhysics();
        updateDegradation();
        distributeSteam();
        emitOperationalRadiation(serverLevel);

        if (shouldCatastrophicallyFail()) {
            mode = ReactorOperationalMode.DISASTER;
            DisasterManager.triggerCatastrophicMeltdown(serverLevel, worldPosition, heat, steamPressure, graphiteFire);
            serverLevel.removeBlock(worldPosition, false);
            return;
        }

        if (steamPressure > RBMKConfig.STEAM_PRESSURE_LIMIT.get() * 0.82D) {
            DisasterManager.triggerRupture(serverLevel, worldPosition, 7.5F, 1.25D);
            steamPressure *= 0.55D;
            containmentDamage = Mth.clamp(containmentDamage + 0.08D, 0D, 1.0D);
        }

        setChanged();
    }

    public void applyControlProfile(ReactorOperationalMode profile) {
        mode = profile;
        switch (profile) {
            case STANDBY -> {
                controlRodInsertion = 0.82D;
                coolantFlow = 0.55D;
            }
            case STARTUP -> {
                controlRodInsertion = 0.55D;
                coolantFlow = 0.75D;
            }
            case POWER_ASCENT -> {
                controlRodInsertion = 0.3D;
                coolantFlow = 0.86D;
            }
            case GRID_LOAD -> {
                controlRodInsertion = 0.22D;
                coolantFlow = 0.92D;
            }
            case EMERGENCY_SHUTDOWN -> {
                scrammed = true;
                controlRodInsertion = 1.0D;
                coolantFlow = 1.0D;
            }
            case DISASTER -> {
                controlRodInsertion = 0D;
                coolantFlow = 0D;
            }
        }
    }

    public void applyManualControls(double rodTarget, double coolantTarget) {
        controlRodInsertion = Mth.clamp(rodTarget, 0D, 1D);
        coolantFlow = Mth.clamp(coolantTarget, 0D, 1D);
    }

    public ReactorTelemetry getTelemetry() {
        double positiveVoid = Mth.clamp((1.0D - coolantFlow) * 1.95D, 0D, 1.85D);
        double thermalPower = heat * (0.55D + neutronFlux * 0.08D) * (1 + fuelChannels * 0.015D);
        return new ReactorTelemetry(thermalPower, neutronFlux, heat, steamPressure, positiveVoid, fuelBurnup, structuralIntegrity, scrammed, mode);
    }

    private void scanInfrastructure() {
        if (level == null || level.getGameTime() % 20 != 0) {
            return;
        }

        int fuels = 0;
        int rods = 0;
        int pumps = 0;
        int turbines = 0;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int x = -8; x <= 8; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -8; z <= 8; z++) {
                    cursor.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                    BlockState state = level.getBlockState(cursor);
                    if (state.is(RBMKBlocks.FUEL_CHANNEL.get())) fuels++;
                    if (state.is(RBMKBlocks.CONTROL_ROD_COLUMN.get())) rods++;
                    if (state.is(RBMKBlocks.WATER_PUMP.get())) pumps++;
                    if (state.is(RBMKBlocks.STEAM_TURBINE.get())) turbines++;
                }
            }
        }

        fuelChannels = fuels;
        controlColumns = rods;
        pumpCount = pumps;
        turbineCount = turbines;
    }

    private void simulateCorePhysics() {
        double insertionPenalty = (controlRodInsertion * 1.8D) + controlColumns * 0.004D;
        double positiveVoidCoefficient = Mth.clamp((1.0D - coolantFlow) * 1.95D, 0D, 1.85D);
        double fuelBonus = Math.min(2.5D, fuelChannels * 0.02D);
        double pumpedCoolant = Math.min(0.55D, pumpCount * 0.025D);

        double reactivity = 1.08D - insertionPenalty + positiveVoidCoefficient + fuelBonus - xenonPoisoning - iodinePit;

        if (scrammed) {
            reactivity -= 0.45D;
            controlRodInsertion = Mth.clamp(controlRodInsertion + 0.02D, 0, 1.0D);
        }

        if (graphiteFire > 0.2D) {
            reactivity += 0.02D * graphiteFire;
        }

        neutronFlux = Mth.clamp(neutronFlux + reactivity * 0.022D, 0D, 30D);

        double generatedHeat = RBMKConfig.BASE_HEAT_GAIN.get() * neutronFlux * (1D + positiveVoidCoefficient + graphiteFire * 0.4D);
        double decayHeat = fuelBurnup * (0.8D + fuelChannels * 0.003D);
        double cooledHeat = RBMKConfig.PASSIVE_COOLING.get() + (coolantFlow + pumpedCoolant) * 3.8D;

        heat = Mth.clamp(heat + generatedHeat + decayHeat - cooledHeat, 20D, 7000D);
        steamPressure = Mth.clamp(steamPressure + (heat * 0.0085D) + (fuelChannels * 0.02D) - (coolantFlow + pumpedCoolant) * 1.2D + containmentDamage * 0.4D, 0D, 2600D);

        steamOutput = Math.max(0D, steamPressure * 0.04D + heat * 0.01D);

        fuelBurnup = Mth.clamp(fuelBurnup + neutronFlux * 0.00035D + fuelChannels * 0.00004D, 0D, 1.0D);
        xenonPoisoning = Mth.clamp(xenonPoisoning + neutronFlux * 0.0007D - 0.0005D, 0D, 0.6D);
        iodinePit = Mth.clamp(iodinePit + (scrammed ? 0.0018D : -0.0012D), 0D, 0.5D);

        graphiteFire = Mth.clamp(graphiteFire + (heat > 1100 ? 0.004D : -0.003D), 0D, 1.0D);

        if (heat > 960D && !scrammed) {
            scrammed = true;
            controlRodInsertion = 1.0D;
            mode = ReactorOperationalMode.EMERGENCY_SHUTDOWN;
        }

        if (scrammed && heat < 420D && steamPressure < 120D) {
            scrammed = false;
            mode = ReactorOperationalMode.STANDBY;
            controlRodInsertion = 0.78D;
        }
    }

    private void distributeSteam() {
        if (level == null || turbineCount <= 0 || steamOutput <= 0) {
            return;
        }

        double each = steamOutput / turbineCount;
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = -10; x <= 10; x++) {
            for (int y = -5; y <= 5; y++) {
                for (int z = -10; z <= 10; z++) {
                    cursor.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                    if (level.getBlockEntity(cursor) instanceof TurbineBlockEntity turbine) {
                        turbine.addSteam(each * 0.02D);
                    }
                }
            }
        }
        steamPressure = Math.max(0D, steamPressure - steamOutput * 0.015D);
    }

    private void updateDegradation() {
        double stress = (Math.max(0, heat - 650D) / 2400D) + (steamPressure / 3000D) + graphiteFire * 0.015D + fuelBurnup * 0.003D;
        structuralIntegrity = Mth.clamp(structuralIntegrity - stress * 0.0028D, 0D, 1.0D);
        containmentDamage = Mth.clamp(containmentDamage + stress * 0.0014D, 0D, 1.0D);
    }

    private void emitOperationalRadiation(ServerLevel serverLevel) {
        double intensity = Math.max(0.1D, neutronFlux * 0.08D + heat * 0.00055D + graphiteFire * 0.9D + containmentDamage + fuelChannels * 0.015D);
        RadiationManager.addFallout(serverLevel, worldPosition, intensity * 0.09D, false);
    }

    private boolean shouldCatastrophicallyFail() {
        return heat >= RBMKConfig.MELTDOWN_HEAT.get()
                || steamPressure >= RBMKConfig.STEAM_PRESSURE_LIMIT.get() * 1.65D
                || structuralIntegrity <= 0.08D
                || (fuelBurnup >= 0.95D && heat > RBMKConfig.MELTDOWN_HEAT.get() * 0.8D);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        controlRodInsertion = tag.getDouble("ControlRods");
        coolantFlow = tag.getDouble("CoolantFlow");
        neutronFlux = tag.getDouble("NeutronFlux");
        heat = tag.getDouble("Heat");
        steamPressure = tag.getDouble("SteamPressure");
        xenonPoisoning = tag.getDouble("XenonPoisoning");
        iodinePit = tag.getDouble("IodinePit");
        fuelBurnup = tag.getDouble("FuelBurnup");
        structuralIntegrity = tag.getDouble("StructuralIntegrity");
        containmentDamage = tag.getDouble("ContainmentDamage");
        graphiteFire = tag.getDouble("GraphiteFire");
        steamOutput = tag.getDouble("SteamOutput");
        scrammed = tag.getBoolean("Scrammed");
        mode = ReactorOperationalMode.valueOf(tag.getString("Mode").isEmpty() ? ReactorOperationalMode.STARTUP.name() : tag.getString("Mode"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putDouble("ControlRods", controlRodInsertion);
        tag.putDouble("CoolantFlow", coolantFlow);
        tag.putDouble("NeutronFlux", neutronFlux);
        tag.putDouble("Heat", heat);
        tag.putDouble("SteamPressure", steamPressure);
        tag.putDouble("XenonPoisoning", xenonPoisoning);
        tag.putDouble("IodinePit", iodinePit);
        tag.putDouble("FuelBurnup", fuelBurnup);
        tag.putDouble("StructuralIntegrity", structuralIntegrity);
        tag.putDouble("ContainmentDamage", containmentDamage);
        tag.putDouble("GraphiteFire", graphiteFire);
        tag.putDouble("SteamOutput", steamOutput);
        tag.putBoolean("Scrammed", scrammed);
        tag.putString("Mode", mode.name());
    }
}
