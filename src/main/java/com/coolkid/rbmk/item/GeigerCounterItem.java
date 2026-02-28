package com.coolkid.rbmk.item;

import com.coolkid.rbmk.radiation.RadiationManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GeigerCounterItem extends Item {
    public GeigerCounterItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            double localDose = RadiationManager.computeDoseForPosition(serverLevel, player.position());
            double cumulative = RadiationManager.getCumulativeDose(player.getUUID());
            ChatFormatting color = localDose > 3.0D ? ChatFormatting.RED : localDose > 1.0D ? ChatFormatting.GOLD : ChatFormatting.GREEN;
            player.displayClientMessage(Component.literal(String.format("Geiger Local: %.2f mSv/t | Cum: %.2f Sv", localDose, cumulative)).withStyle(color), true);
        }
        return InteractionResultHolder.success(stack);
    }
}
