package com.coolkid.rbmk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class AcuteRadiationSyndromeEffect extends MobEffect {
    public AcuteRadiationSyndromeEffect() {
        super(MobEffectCategory.HARMFUL, 0x44FF22);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.hurt(entity.damageSources().wither(), 1.2F + amplifier);
        entity.setDeltaMovement(entity.getDeltaMovement().multiply(0.86D, 1.0D, 0.86D));
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % Math.max(6, 16 - amplifier * 2) == 0;
    }
}
