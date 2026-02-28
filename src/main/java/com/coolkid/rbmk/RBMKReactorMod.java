package com.coolkid.rbmk;

import com.coolkid.rbmk.config.RBMKConfig;
import com.coolkid.rbmk.event.ServerEventHandler;
import com.coolkid.rbmk.init.RBMKBlockEntities;
import com.coolkid.rbmk.init.RBMKBlocks;
import com.coolkid.rbmk.init.RBMKEffects;
import com.coolkid.rbmk.init.RBMKItems;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(RBMKReactorMod.MOD_ID)
public class RBMKReactorMod {
    public static final String MOD_ID = "rbmkreactor";

    public RBMKReactorMod() {
        var modBus = FMLJavaModLoadingContext.get().getModEventBus();

        RBMKBlocks.BLOCKS.register(modBus);
        RBMKItems.ITEMS.register(modBus);
        RBMKBlockEntities.BLOCK_ENTITIES.register(modBus);
        RBMKEffects.EFFECTS.register(modBus);

        modBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(new ServerEventHandler());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, RBMKConfig.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        // reserved for network packets and other setup hooks
    }
}
