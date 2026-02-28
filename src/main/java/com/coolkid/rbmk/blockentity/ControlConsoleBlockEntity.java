package com.coolkid.rbmk.blockentity;

import com.coolkid.rbmk.init.RBMKBlockEntities;
import com.coolkid.rbmk.reactor.ReactorOperationalMode;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ControlConsoleBlockEntity extends BlockEntity {
    private ReactorOperationalMode profile = ReactorOperationalMode.STARTUP;
    private int cooldownTicks;

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
        }
    }

    public void cycleOperationProfile(ServerPlayer player) {
        if (cooldownTicks > 0) {
            return;
        }

        ReactorOperationalMode[] modes = ReactorOperationalMode.values();
        profile = modes[(profile.ordinal() + 1) % modes.length];
        cooldownTicks = 10;

        player.displayClientMessage(Component.literal("RBMK Console profile: " + profile.name())
                .withStyle(ChatFormatting.YELLOW), true);
    }

    private ReactorCoreBlockEntity findNearbyCore(ServerLevel level) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int x = -12; x <= 12; x++) {
            for (int y = -6; y <= 6; y++) {
                for (int z = -12; z <= 12; z++) {
                    cursor.set(worldPosition.getX() + x, worldPosition.getY() + y, worldPosition.getZ() + z);
                    if (level.getBlockEntity(cursor) instanceof ReactorCoreBlockEntity reactor) {
                        return reactor;
                    }
                }
            }
        }
        return null;
    }
}
