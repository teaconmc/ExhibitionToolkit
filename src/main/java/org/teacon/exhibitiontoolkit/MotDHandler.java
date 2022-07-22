package org.teacon.exhibitiontoolkit;

import net.minecraft.network.chat.Component;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "exhibition_toolkit")
public class MotDHandler {

    static Component motd = null;

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (motd != null) {
            event.getPlayer().displayClientMessage(motd, false);
        }
    }
}
