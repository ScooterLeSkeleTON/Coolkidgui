package com.coolkid.rbmk.init;

import com.coolkid.rbmk.RBMKReactorMod;
import com.coolkid.rbmk.effect.RadiationSicknessEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RBMKEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, RBMKReactorMod.MOD_ID);

    public static final RegistryObject<MobEffect> RADIATION_SICKNESS = EFFECTS.register("radiation_sickness", RadiationSicknessEffect::new);

    private RBMKEffects() {
    }
}
