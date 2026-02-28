package com.coolkid.rbmk.init;

import com.coolkid.rbmk.RBMKReactorMod;
import com.coolkid.rbmk.menu.ControlPanelMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RBMKMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, RBMKReactorMod.MOD_ID);

    public static final RegistryObject<MenuType<ControlPanelMenu>> CONTROL_PANEL = MENUS.register("control_panel",
            () -> IForgeMenuType.create(ControlPanelMenu::new));

    private RBMKMenus() {
    }
}
