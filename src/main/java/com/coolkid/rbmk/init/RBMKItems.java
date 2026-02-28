package com.coolkid.rbmk.init;

import com.coolkid.rbmk.RBMKReactorMod;
import com.coolkid.rbmk.item.GeigerCounterItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RBMKItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, RBMKReactorMod.MOD_ID);

    public static final RegistryObject<Item> REACTOR_CORE_ITEM = ITEMS.register("reactor_core", () -> new BlockItem(RBMKBlocks.REACTOR_CORE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CONTROL_CONSOLE_ITEM = ITEMS.register("control_console", () -> new BlockItem(RBMKBlocks.CONTROL_CONSOLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> GRAPHITE_CASING_ITEM = ITEMS.register("graphite_casing", () -> new BlockItem(RBMKBlocks.GRAPHITE_CASING.get(), new Item.Properties()));
    public static final RegistryObject<Item> FUEL_CHANNEL_ITEM = ITEMS.register("fuel_channel", () -> new BlockItem(RBMKBlocks.FUEL_CHANNEL.get(), new Item.Properties()));
    public static final RegistryObject<Item> CONTROL_ROD_COLUMN_ITEM = ITEMS.register("control_rod_column", () -> new BlockItem(RBMKBlocks.CONTROL_ROD_COLUMN.get(), new Item.Properties()));
    public static final RegistryObject<Item> STEAM_TURBINE_ITEM = ITEMS.register("steam_turbine", () -> new BlockItem(RBMKBlocks.STEAM_TURBINE.get(), new Item.Properties()));
    public static final RegistryObject<Item> WATER_PUMP_ITEM = ITEMS.register("water_pump", () -> new BlockItem(RBMKBlocks.WATER_PUMP.get(), new Item.Properties()));
    public static final RegistryObject<Item> GEIGER_COUNTER = ITEMS.register("geiger_counter", () -> new GeigerCounterItem(new Item.Properties().stacksTo(1)));

    private RBMKItems() {
    }
}
