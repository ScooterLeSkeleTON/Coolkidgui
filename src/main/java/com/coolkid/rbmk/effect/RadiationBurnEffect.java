package com.coolkid.rbmk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class RadiationBurnEffect extends MobEffect {
    public RadiationBurnEffect() {
        super(MobEffectCategory.HARMFUL, 0x99FF55);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        entity.hurt(entity.damageSources().magic(), 0.8F + amplifier * 0.8F);
        if (entity.getRandom().nextFloat() < 0.3F + amplifier * 0.1F) {
            entity.setSecondsOnFire(1 + amplifier);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return duration % Math.max(8, 20 - amplifier * 2) == 0;
    }
}
