package com.coolkid.rbmk.init;

import com.coolkid.rbmk.RBMKReactorMod;
import com.coolkid.rbmk.block.ControlConsoleBlock;
import com.coolkid.rbmk.block.ReactorCoreBlock;
import com.coolkid.rbmk.block.TurbineBlock;
import com.coolkid.rbmk.block.WaterPumpBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RBMKBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, RBMKReactorMod.MOD_ID);

    public static final RegistryObject<Block> REACTOR_CORE = BLOCKS.register("reactor_core",
            () -> new ReactorCoreBlock(BlockBehaviour.Properties.of()
                    .strength(8.0F, 1200F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 8)));

    public static final RegistryObject<Block> CONTROL_CONSOLE = BLOCKS.register("control_console",
            () -> new ControlConsoleBlock(BlockBehaviour.Properties.of()
                    .strength(5.0F, 30F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()
                    .lightLevel(state -> 8)));

    public static final RegistryObject<Block> GRAPHITE_CASING = BLOCKS.register("graphite_casing",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(4.5F, 12F)
                    .sound(SoundType.DEEPSLATE_BRICKS)));

    public static final RegistryObject<Block> FUEL_CHANNEL = BLOCKS.register("fuel_channel",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(6.0F, 16F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> CONTROL_ROD_COLUMN = BLOCKS.register("control_rod_column",
            () -> new Block(BlockBehaviour.Properties.of()
                    .strength(6.5F, 18F)
                    .sound(SoundType.NETHERITE_BLOCK)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> STEAM_TURBINE = BLOCKS.register("steam_turbine",
            () -> new TurbineBlock(BlockBehaviour.Properties.of()
                    .strength(5.5F, 20F)
                    .sound(SoundType.COPPER)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> WATER_PUMP = BLOCKS.register("water_pump",
            () -> new WaterPumpBlock(BlockBehaviour.Properties.of()
                    .strength(4.5F, 16F)
                    .sound(SoundType.IRON)
                    .requiresCorrectToolForDrops()));

    private RBMKBlocks() {
    }
}
