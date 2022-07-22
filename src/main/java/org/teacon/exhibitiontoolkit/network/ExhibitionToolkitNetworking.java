package org.teacon.exhibitiontoolkit.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class ExhibitionToolkitNetworking {

    public static SimpleChannel channel = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("exhibition_toolkit", "channel"),
            () -> "0.1.0", "0.1.0"::equals, "0.1.0"::equals);

    public static void init() {
        channel.registerMessage(0, UpdatePowerSupplyData.class, UpdatePowerSupplyData::write,
                UpdatePowerSupplyData::new, UpdatePowerSupplyData::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
