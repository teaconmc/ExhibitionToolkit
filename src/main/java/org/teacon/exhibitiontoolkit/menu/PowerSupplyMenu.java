package org.teacon.exhibitiontoolkit.menu;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.teacon.exhibitiontoolkit.ExhibitionToolkit;
import org.teacon.exhibitiontoolkit.block.PowerSupplyBlock;

public class PowerSupplyMenu extends AbstractContainerMenu {

    public PowerSupplyBlock.Data dataHolder;

    public PowerSupplyMenu(int containerId, Inventory playerInv, PowerSupplyBlock.Data dataHolder) {
        super(ExhibitionToolkit.POWER_SUPPLY_MENU.get(), containerId);
        this.dataHolder = dataHolder;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    public record Provider(PowerSupplyBlock.Data data) implements MenuProvider {

        @Override
        public Component getDisplayName() {
            return TextComponent.EMPTY;
        }

        @Override
        public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
            return new PowerSupplyMenu(containerId, inv, this.data);
        }
    }
}
