package com.coolkid.rbmk.init;

import com.coolkid.rbmk.RBMKReactorMod;
import com.coolkid.rbmk.effect.AcuteRadiationSyndromeEffect;
import com.coolkid.rbmk.effect.RadiationBurnEffect;
import com.coolkid.rbmk.effect.RadiationSicknessEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RBMKEffects {
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, RBMKReactorMod.MOD_ID);

    public static final RegistryObject<MobEffect> RADIATION_SICKNESS = EFFECTS.register("radiation_sickness", RadiationSicknessEffect::new);
    public static final RegistryObject<MobEffect> RADIATION_BURN = EFFECTS.register("radiation_burn", RadiationBurnEffect::new);
    public static final RegistryObject<MobEffect> ACUTE_RADIATION_SYNDROME = EFFECTS.register("acute_radiation_syndrome", AcuteRadiationSyndromeEffect::new);

    private RBMKEffects() {
    }
}
