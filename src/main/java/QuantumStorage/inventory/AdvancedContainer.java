package QuantumStorage.inventory;

import QuantumStorage.tiles.AdvancedTileEntity;
import QuantumStorage.tiles.chests.TileChestDiamond;
import QuantumStorage.tiles.chests.TileChestGold;
import QuantumStorage.tiles.chests.TileChestIron;
import invtweaks.api.container.ChestContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Slot;
import reborncore.common.container.RebornContainer;

import static com.google.common.base.Preconditions.*;

/**
 * Created by Gigabit101 on 17/03/2017.
 */
@ChestContainer(isLargeChest = true)
public class AdvancedContainer extends RebornContainer
{
    public AdvancedTileEntity machine;
    
    public AdvancedContainer(EntityPlayer player, AdvancedTileEntity machine)
    {
        super(machine);
        this.machine = machine;

        machine.addWatcher(player);

        if (machine.getSlots() != null)
        {
            for (Slot s : machine.getSlots())
            {
                addSlotToContainer(s);
            }
        }
        drawPlayersInv(player, machine.inventoryOffsetX(), machine.inventoryOffsetY());
        drawPlayersHotBar(player, machine.inventoryOffsetX(), machine.inventoryOffsetY() + 58);
    }

    @Override
    public void onContainerClosed(EntityPlayer playerIn) {
        this.machine.removeWatcher(playerIn);

        super.onContainerClosed(playerIn);
    }

    @ChestContainer.RowSizeCallback
    public int getNumColumns()
    {
        if (machine instanceof TileChestIron)
        {
            return 9;
        }
        if (machine instanceof TileChestGold)
        {
            return 9;
        }
        if (machine instanceof TileChestDiamond)
        {
            return 13;
        }
        return 0;
    }
    
    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return machine != null;
    }
}
