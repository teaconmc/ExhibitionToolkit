package org.teacon.exhibitiontoolkit;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.teacon.exhibitiontoolkit.screen.PowerSupplyScreen;

@Mod.EventBusSubscriber(modid = "exhibition_toolkit", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ExhibitionToolkitClientSetup {

    @SubscribeEvent
    public static void screen(FMLClientSetupEvent event) {
        MenuScreens.register(ExhibitionToolkit.POWER_SUPPLY_MENU.get(), PowerSupplyScreen::new);
    }
}
