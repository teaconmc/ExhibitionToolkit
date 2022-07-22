package org.teacon.exhibitiontoolkit;

import com.mojang.datafixers.DSL;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.teacon.exhibitiontoolkit.block.PowerSupplyBlock;
import org.teacon.exhibitiontoolkit.block.TrashCanBlock;
import org.teacon.exhibitiontoolkit.menu.PowerSupplyMenu;
import org.teacon.exhibitiontoolkit.network.ExhibitionToolkitNetworking;

@Mod("exhibition_toolkit")
public class ExhibitionToolkit {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "exhibition_toolkit");

    public static final RegistryObject<Block> TRASH_CAN = BLOCKS.register("trash_can",
            () -> new TrashCanBlock(BlockBehaviour.Properties.of(Material.METAL)));

    public static final RegistryObject<Block> POWER_SUPPLY = BLOCKS.register("power_supply",
            () -> new PowerSupplyBlock(BlockBehaviour.Properties.of(Material.METAL)));

    //public static final RegistryObject<Block> POWER_SINK = BLOCKS.register("power_sink",
    //        () -> new TrashCanBlock(BlockBehaviour.Properties.of(Material.METAL)));

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, "exhibition_toolkit");

    public static final RegistryObject<Item> TRASH_CAN_ITEM = ITEMS.register("trash_can",
            () -> new BlockItem(TRASH_CAN.get(), new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)));

    public static final RegistryObject<Item> POWER_SUPPLY_ITEM = ITEMS.register("power_supply",
            () -> new BlockItem(POWER_SUPPLY.get(), new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

    public static final RegistryObject<Item> USELESS_STICK = ITEMS.register("useless_stick",
            () -> new Item(new Item.Properties()) {
                @Override
                public boolean isFoil(ItemStack stack) {
                    return true;
                }
            });

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, "exhibition_toolkit");

    public static final RegistryObject<BlockEntityType<PowerSupplyBlock.Entity>> POWER_SUPPLY_BLOCK_ENTITY = BLOCK_ENTITIES.register("power_supply",
            () -> BlockEntityType.Builder.of(PowerSupplyBlock.Entity::new, POWER_SUPPLY.get()).build(DSL.remainderType()));

    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.CONTAINERS, "exhibition_toolkit");

    public static final RegistryObject<MenuType<PowerSupplyMenu>> POWER_SUPPLY_MENU = MENUS.register("power_supply",
            () -> IForgeMenuType.create(((windowId, inv, data) -> {
                final var dataHolder = new PowerSupplyBlock.Data();
                dataHolder.status = data.readVarInt();
                dataHolder.power = data.readVarInt();
                return new PowerSupplyMenu(windowId, inv, dataHolder);
            })));

    public static ForgeConfigSpec.ConfigValue<String> motdContent;

    public ExhibitionToolkit() {
        var bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        MENUS.register(bus);
        var config = new ForgeConfigSpec.Builder();
        motdContent = config.comment("Message-of-the-day content.").define("motd", "");
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, config.build());
        bus.addListener(EventPriority.NORMAL, false, ModConfigEvent.Reloading.class, ExhibitionToolkit::onConfigReload);
        ExhibitionToolkitNetworking.init();
    }

    public static void onConfigReload(ModConfigEvent.Reloading event) {
        var motd = motdContent.get();
        if (motd != null && !motd.isEmpty()) {
            MotDHandler.motd = new TextComponent(motd.trim());
        }
    }
}
