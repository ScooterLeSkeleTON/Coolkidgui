package com.coolkid.rbmk.client.screen;

import com.coolkid.rbmk.menu.ControlPanelMenu;
import com.coolkid.rbmk.reactor.ReactorOperationalMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class ControlPanelScreen extends AbstractContainerScreen<ControlPanelMenu> {
    public ControlPanelScreen(ControlPanelMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 220;
        this.imageHeight = 190;
    }

    @Override
    protected void init() {
        super.init();
        int x = leftPos;
        int y = topPos;

        addRenderableWidget(Button.builder(Component.literal("Cycle Mode"), b -> send(0)).bounds(x + 12, y + 140, 90, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Rods +"), b -> send(1)).bounds(x + 12, y + 164, 44, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Rods -"), b -> send(2)).bounds(x + 58, y + 164, 44, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cool +"), b -> send(3)).bounds(x + 108, y + 164, 44, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Cool -"), b -> send(4)).bounds(x + 154, y + 164, 44, 20).build());
        addRenderableWidget(Button.builder(Component.literal("SCRAM"), b -> send(5)).bounds(x + 108, y + 140, 90, 20).build());
    }

    private void send(int id) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
            menu.refreshData();
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.fill(leftPos, topPos, leftPos + imageWidth, topPos + imageHeight, 0xE0101010);
        guiGraphics.fill(leftPos + 6, topPos + 6, leftPos + imageWidth - 6, topPos + 132, 0xA0000000);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        menu.refreshData();
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        renderTooltip(graphics, mouseX, mouseY);

        int lx = leftPos + 12;
        int ly = topPos + 12;
        ReactorOperationalMode mode = ReactorOperationalMode.values()[Math.max(0, Math.min(menu.getModeOrdinal(), ReactorOperationalMode.values().length - 1))];

        graphics.drawString(font, "RBMK Master Panel", lx, ly, 0x00FFAA, false);
        graphics.drawString(font, String.format("Heat: %.1f C", menu.getHeat()), lx, ly + 14, 0xFFFFFF, false);
        graphics.drawString(font, String.format("Pressure: %.1f bar", menu.getPressure()), lx, ly + 26, 0xFFFFFF, false);
        graphics.drawString(font, String.format("Flux: %.2f", menu.getFlux()), lx, ly + 38, 0xFFFFFF, false);
        graphics.drawString(font, String.format("Dose: %.2f", menu.getDose()), lx, ly + 50, 0x88FF88, false);
        graphics.drawString(font, String.format("Rods: %.2f", menu.getRods()), lx, ly + 62, 0xFFD38A, false);
        graphics.drawString(font, String.format("Coolant: %.2f", menu.getCoolant()), lx, ly + 74, 0x8AD3FF, false);
        graphics.drawString(font, "Mode: " + mode.name(), lx, ly + 86, 0xFFEE58, false);
        graphics.drawString(font, "SCRAM: " + (menu.isScrammed() ? "ACTIVE" : "OFF"), lx, ly + 98, menu.isScrammed() ? 0xFF4444 : 0x88FF88, false);
    }
}
