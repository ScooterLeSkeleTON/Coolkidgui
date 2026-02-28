package com.coolkid.rbmk.init;

import com.coolkid.rbmk.RBMKReactorMod;
import com.coolkid.rbmk.blockentity.ControlConsoleBlockEntity;
import com.coolkid.rbmk.blockentity.ReactorCoreBlockEntity;
import com.coolkid.rbmk.blockentity.TurbineBlockEntity;
import com.coolkid.rbmk.blockentity.WaterPumpBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RBMKBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RBMKReactorMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<ReactorCoreBlockEntity>> REACTOR_CORE = BLOCK_ENTITIES.register("reactor_core",
            () -> BlockEntityType.Builder.of(ReactorCoreBlockEntity::new, RBMKBlocks.REACTOR_CORE.get()).build(null));
    public static final RegistryObject<BlockEntityType<ControlConsoleBlockEntity>> CONTROL_CONSOLE = BLOCK_ENTITIES.register("control_console",
            () -> BlockEntityType.Builder.of(ControlConsoleBlockEntity::new, RBMKBlocks.CONTROL_CONSOLE.get()).build(null));
    public static final RegistryObject<BlockEntityType<TurbineBlockEntity>> STEAM_TURBINE = BLOCK_ENTITIES.register("steam_turbine",
            () -> BlockEntityType.Builder.of(TurbineBlockEntity::new, RBMKBlocks.STEAM_TURBINE.get()).build(null));
    public static final RegistryObject<BlockEntityType<WaterPumpBlockEntity>> WATER_PUMP = BLOCK_ENTITIES.register("water_pump",
            () -> BlockEntityType.Builder.of(WaterPumpBlockEntity::new, RBMKBlocks.WATER_PUMP.get()).build(null));

    private RBMKBlockEntities() {
    }
}
