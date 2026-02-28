package com.coolkid.rbmk.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;

public class RadiationSicknessEffect extends MobEffect {
    public RadiationSicknessEffect() {
        super(MobEffectCategory.HARMFUL, 0x73FF7A);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().random.nextFloat() < 0.08F + amplifier * 0.06F) {
            entity.hurt(entity.damageSources().magic(), 1.0F + amplifier);
        }

        if (entity.level().random.nextFloat() < 0.03F + amplifier * 0.02F) {
            entity.setSecondsOnFire(1);
        }
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        int interval = Math.max(10, 40 - amplifier * 8);
        return duration % interval == 0;
    }

    @Override
    public void removeAttributeModifiers(LivingEntity entity, AttributeMap map, int amplifier) {
        super.removeAttributeModifiers(entity, map, amplifier);
    }
}
