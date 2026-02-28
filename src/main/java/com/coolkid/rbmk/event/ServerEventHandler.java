package com.coolkid.rbmk.event;

import com.coolkid.rbmk.radiation.RadiationManager;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerEventHandler {
    @SubscribeEvent
    public void onServerLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) {
            return;
        }

        RadiationManager.tickPlayers(level);
    }
}
