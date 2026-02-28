package com.coolkid.rbmk.menu;

import com.coolkid.rbmk.blockentity.ControlConsoleBlockEntity;
import com.coolkid.rbmk.init.RBMKMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;

public class ControlPanelMenu extends AbstractContainerMenu {
    private final ControlConsoleBlockEntity console;
    private final ContainerData data;

    public ControlPanelMenu(int id, Inventory inventory, FriendlyByteBuf buffer) {
        this(id, inventory, (ControlConsoleBlockEntity) inventory.player.level().getBlockEntity(buffer.readBlockPos()));
    }

    public ControlPanelMenu(int id, Inventory inventory, ControlConsoleBlockEntity console) {
        super(RBMKMenus.CONTROL_PANEL.get(), id);
        this.console = console;
        this.data = new SimpleContainerData(8);
        this.addDataSlots(data);
        refreshData();
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (console == null) {
            return false;
        }

        switch (id) {
            case 0 -> console.cycleOperationProfile(player);
            case 1 -> console.adjustRods(0.05D);
            case 2 -> console.adjustRods(-0.05D);
            case 3 -> console.adjustCoolant(0.05D);
            case 4 -> console.adjustCoolant(-0.05D);
            case 5 -> console.forceScram();
            default -> {
                return false;
            }
        }
        refreshData();
        return true;
    }

    public void refreshData() {
        if (console == null) {
            return;
        }
        data.set(0, (int) (console.getHeat() * 10));
        data.set(1, (int) (console.getPressure() * 10));
        data.set(2, (int) (console.getFlux() * 100));
        data.set(3, (int) (console.getDose() * 100));
        data.set(4, (int) (console.getRodTarget() * 1000));
        data.set(5, (int) (console.getCoolantTarget() * 1000));
        data.set(6, console.getModeOrdinal());
        data.set(7, console.isScrammed() ? 1 : 0);
    }

    public double getHeat() { return data.get(0) / 10D; }
    public double getPressure() { return data.get(1) / 10D; }
    public double getFlux() { return data.get(2) / 100D; }
    public double getDose() { return data.get(3) / 100D; }
    public double getRods() { return data.get(4) / 1000D; }
    public double getCoolant() { return data.get(5) / 1000D; }
    public int getModeOrdinal() { return data.get(6); }
    public boolean isScrammed() { return data.get(7) == 1; }

    @Override
    public boolean stillValid(Player player) {
        return console != null && player.distanceToSqr(console.getBlockPos().getX() + 0.5, console.getBlockPos().getY() + 0.5, console.getBlockPos().getZ() + 0.5) < 144;
    }
}
