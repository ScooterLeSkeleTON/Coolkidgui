package com.coolkid.rbmk.blockentity;

import com.coolkid.rbmk.init.RBMKBlockEntities;
import com.coolkid.rbmk.menu.ControlPanelMenu;
import com.coolkid.rbmk.radiation.RadiationManager;
import com.coolkid.rbmk.reactor.ReactorOperationalMode;
import com.coolkid.rbmk.reactor.ReactorTelemetry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ControlConsoleBlockEntity extends BlockEntity implements MenuProvider {
    private ReactorOperationalMode profile = ReactorOperationalMode.STARTUP;
    private int cooldownTicks;
    private double rodTarget = 0.55D;
    private double coolantTarget = 0.8D;

    private ReactorTelemetry cachedTelemetry = new ReactorTelemetry(0, 0, 20, 0, 0, 0, 1, false, ReactorOperationalMode.STANDBY);

    public ControlConsoleBlockEntity(BlockPos pos, BlockState state) {
        super(RBMKBlockEntities.CONTROL_CONSOLE.get(), pos, state);
    }

    public void tickServer() {
        if (cooldownTicks > 0) {
            cooldownTicks--;
        }

        if (!(level instanceof ServerLevel serverLevel) || serverLevel.getGameTime() % 20 != 0) {
            return;
        }

        ReactorCoreBlockEntity reactor = findNearbyCore(serverLevel);
        if (reactor != null) {
            reactor.applyControlProfile(profile);
            reactor.applyManualControls(rodTarget, coolantTarget);
            cachedTelemetry = reactor.getTelemetry();
        }
    }

    public void cycleOperationProfile(Player player) {
        if (cooldownTicks > 0) {
            return;
        }

        ReactorOperationalMode[] modes = ReactorOperationalMode.values();
        profile = modes[(profile.ordinal() + 1) % modes.length];
        cooldownTicks = 5;
        setChanged();
    }

    public void adjustRods(double delta) {
        rodTarget = Math.max(0.0D, Math.min(1.0D, rodTarget + delta));
        setChanged();
    }

    public void adjustCoolant(double delta) {
        coolantTarget = Math.max(0.0D, Math.min(1.0D, coolantTarget + delta));
        setChanged();
    }

    public void forceScram() {
        profile = ReactorOperationalMode.EMERGENCY_SHUTDOWN;
    }

    public double getHeat() { return cachedTelemetry.coreHeat(); }
    public double getPressure() { return cachedTelemetry.steamPressure(); }
    public double getFlux() { return cachedTelemetry.neutronFlux(); }
    public double getDose() { return level instanceof ServerLevel s ? RadiationManager.computeDoseForPosition(s, net.minecraft.world.phys.Vec3.atCenterOf(worldPosition)) : 0D; }
    public double getRodTarget() { return rodTarget; }
    public double getCoolantTarget() { return coolantTarget; }
    public int getModeOrdinal() { return profile.ordinal(); }
    public boolean isScrammed() { return cachedTelemetry.scrammed(); }

    private ReactorCoreBlockEntity findNearbyCore(ServerLevel level) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = -16; x <= 16; x++) {
            for (int y = -8; y <= 8; y++) {
                for (int z = -16; z <= 16; z++) {
                    cursor.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                    if (level.getBlockEntity(cursor) instanceof ReactorCoreBlockEntity reactor) {
                        return reactor;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("RBMK Control Panel");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ControlPanelMenu(id, inventory, this);
    }
}
