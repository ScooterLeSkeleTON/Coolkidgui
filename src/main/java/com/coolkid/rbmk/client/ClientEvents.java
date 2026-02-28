package com.coolkid.rbmk.client;

import com.coolkid.rbmk.RBMKReactorMod;
import com.coolkid.rbmk.client.screen.ControlPanelScreen;
import com.coolkid.rbmk.init.RBMKMenus;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterMenuScreensEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RBMKReactorMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientEvents {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(RBMKMenus.CONTROL_PANEL.get(), ControlPanelScreen::new);
    }

    private ClientEvents() {
    }
}
