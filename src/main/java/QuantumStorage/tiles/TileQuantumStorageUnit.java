package QuantumStorage.tiles;

import QuantumStorage.client.AdvancedGui;
import QuantumStorage.config.ConfigQuantumStorage;
import QuantumStorage.init.ModBlocks;
import QuantumStorage.inventory.DsuInventoryHandler;
import QuantumStorage.inventory.slot.SlotOutputItemHandler;
import QuantumStorage.utils.INBTSerializableIntoExisting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import reborncore.common.util.ItemUtils;
import reborncore.common.util.RebornCraftingHelper;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Gigabit101 on 17/03/2017.
 */
public class TileQuantumStorageUnit extends AdvancedTileEntity implements ITickable
{
    private static final int STORAGE = 0;
    private static final int INPUT = 1;
    private static final int OUTPUT = 2;

    private transient ItemStack lastSentStack = ItemStack.EMPTY;
    private transient int lastSentCount = 0;

    public TileQuantumStorageUnit()
    {
        this.inv = new DsuInventoryHandler();
    }
    
    @Override
    public void update()
    {
        try
        {
            ItemStack outputStack = this.inv.getStackInSlot(OUTPUT);
            ItemStack inputStack = this.inv.getStackInSlot(INPUT);
            ItemStack storageStack = this.inv.getStackInSlot(STORAGE);
            if (!inputStack.isEmpty())
            {
                if (storageStack.isEmpty())
                {
                    inv.setStackInSlot(STORAGE, storageStack = inputStack.copy());
                    inv.setStackInSlot(INPUT, inputStack = ItemStack.EMPTY);
                } else if (ItemUtils.isItemEqual(inputStack, storageStack, true, true))
                {
                    storageStack.grow(inputStack.getCount());
                    inv.setStackInSlot(INPUT, inputStack = ItemStack.EMPTY);
                }
            }
            
            if (!storageStack.isEmpty())
            {
                int size = storageStack.getMaxStackSize();
                if (outputStack.isEmpty())
                {
                    if (storageStack.getCount() >= size)
                    {
                        inv.setStackInSlot(OUTPUT, outputStack = storageStack.copy());
                        outputStack.setCount(size);
                        storageStack.shrink(size);
                    } else
                    {
                        inv.setStackInSlot(OUTPUT, outputStack = storageStack);
                        inv.setStackInSlot(STORAGE, storageStack = ItemStack.EMPTY);
                    }
                }
                if (storageStack.getCount() != 0 && ItemUtils.isItemEqual(storageStack, outputStack, true, true) && outputStack.getCount() <= size - 1)
                {
                    outputStack.grow(1);
                    storageStack.shrink(1);
                }
            }
            handleUpgrades();
            sync();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void sync() {
        DsuInventoryHandler handler = (DsuInventoryHandler) inv;
        if(handler.requestUpdate)
        {
            handler.requestUpdate = false;
            super.sync();
        }
    }

    @Override
    protected boolean pollSyncAllowed(boolean hasWatcher) {
        if (!super.pollSyncAllowed(hasWatcher)) {
            return false;
        }

        ItemStack currentStack = this.inv.getStackInSlot(OUTPUT);
        ItemStack currentStorageStack = this.inv.getStackInSlot(STORAGE);
        int currentCount = currentStack.getCount() + currentStorageStack.getCount();
        if (!ItemUtils.isItemEqual(currentStack, this.lastSentStack, true, true) || (hasWatcher && currentCount != this.lastSentCount)) {
            this.lastSentStack = currentStack;
            this.lastSentCount = currentCount;
            return true;
        }

        return false;
    }

    @Override
    public String getName()
    {
        return "quantum_storage_unit";
    }
    
    @Override
    public List<Slot> getSlots()
    {
        List<Slot> slots = new ArrayList<>();
        slots.add(new SlotItemHandler(inv, 1, 80, 20));
        slots.add(new SlotOutputItemHandler(inv, 2, 80, 70));
        
        return slots;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileQuantumStorageUnit();
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        openGui(playerIn, (AdvancedTileEntity) worldIn.getTileEntity(pos));
        return true;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY, GuiContainer gui, int guiLeft, int guiTop)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY, gui, guiLeft, guiTop);
        if (this.getInv().getStackInSlot(STORAGE) != ItemStack.EMPTY && this.getInv().getStackInSlot(OUTPUT) != null)
        {
            this.getBuilder().drawBigBlueBar((AdvancedGui) gui, 31, 43, this.getInv().getStackInSlot(STORAGE).getCount() + this.getInv().getStackInSlot(OUTPUT).getCount(), Integer.MAX_VALUE, mouseX - guiLeft, mouseY - guiTop, "Stored", getInv().getStackInSlot(OUTPUT).getDisplayName(),
                    this.getBuilder().formatQuantityApprox(this.getInv().getStackInSlot(STORAGE).getCount() + this.getInv().getStackInSlot(OUTPUT).getCount()));
        }
        if (this.getInv().getStackInSlot(STORAGE) == ItemStack.EMPTY && this.getInv().getStackInSlot(OUTPUT) != ItemStack.EMPTY)
        {
            this.getBuilder().drawBigBlueBar((AdvancedGui) gui, 31, 43, this.getInv().getStackInSlot(OUTPUT).getCount(), Integer.MAX_VALUE, mouseX - guiLeft, mouseY - guiTop, "Stored", getInv().getStackInSlot(OUTPUT).getDisplayName(),
                    this.getBuilder().formatQuantityApprox(this.getInv().getStackInSlot(STORAGE).getCount() + this.getInv().getStackInSlot(OUTPUT).getCount()));
        }
    }
    
    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        inv.deserializeNBT(compound);
    }
    
    @Override
    public Block getBlock()
    {
        return ModBlocks.DSU;
    }
    
    @Override
    public void addRecipe()
    {
        if (!ConfigQuantumStorage.disableQuantumStorageUnit)
        {
            RebornCraftingHelper.addShapedOreRecipe(new ItemStack(ModBlocks.DSU),
                    "OOO",
                    "ICI",
                    "III",
                    'I', new ItemStack(Items.IRON_INGOT),
                    'O', new ItemStack(Blocks.OBSIDIAN),
                    'C', new ItemStack(ModBlocks.CHEST_DIAMOND));
            
            RebornCraftingHelper.addShapelessRecipe(new ItemStack(ModBlocks.DSU), new ItemStack(ModBlocks.DSU));
        }
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        INBTSerializableIntoExisting.trySerializeIntoOrMerge(compound, this.inv);
        return compound;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT_MIPPED || layer == BlockRenderLayer.TRANSLUCENT;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return true;
        }
        return super.hasCapability(capability, facing);
    }
    
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inv);
        }
        return super.getCapability(capability, facing);
    }
    
    public void handleUpgrades() {}
    
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced)
    {
        if (!stack.isEmpty() && stack.hasTagCompound())
        {
            if (stack.getTagCompound().getCompoundTag("tileEntity") != null)
            {
                NBTTagList tagList = stack.getTagCompound().getCompoundTag("tileEntity").getTagList("Items", Constants.NBT.TAG_COMPOUND);
                ItemStack stack1 = ItemStack.EMPTY;
                
                NBTTagCompound itemTags = tagList.getCompoundTagAt(0);
                NBTTagCompound itemTags2 = tagList.getCompoundTagAt(2);
                
                int count = itemTags.getInteger("SizeSpecial") + itemTags2.getInteger("SizeSpecial");
                
                stack1 = new ItemStack(itemTags);
                stack1.setCount(count);
                
                if (!stack1.isEmpty())
                {
                    tooltip.add(TextFormatting.GOLD + "Stored Item Type: " + stack1.getCount() + " " + stack1.getDisplayName());
                }
            }
        }
    }
}
