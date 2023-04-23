package QuantumStorage.tiles;

import QuantumStorage.client.AdvancedGui;
import QuantumStorage.config.ConfigQuantumStorage;
import QuantumStorage.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import reborncore.common.util.ItemUtils;
import reborncore.common.util.RebornCraftingHelper;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Gigabit101 on 17/03/2017.
 */
public class TileQuantumTank extends AdvancedTileEntity implements ITickable
{
    final EnhancedFluidTank tank = new EnhancedFluidTank(Integer.MAX_VALUE);

    public transient boolean infiniteWater;

    transient FluidStack lastSentStack = null;
    transient int lastSentAmount = 0;

    public TileQuantumTank() {}
    
    @Override
    public String getName()
    {
        return "quantum_tank";
    }
    
    @Override
    public List<Slot> getSlots()
    {
        return null;
    }
    
    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileQuantumTank();
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (FluidUtil.interactWithFluidHandler(playerIn, hand, worldIn, pos, side))
        {
            return true;
        }
        else if (worldIn.getTileEntity(pos).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side))
        {
            IFluidTank handler = (IFluidTank) worldIn.getTileEntity(pos).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
            if(!playerIn.getHeldItem(hand).isEmpty() && ItemUtils.isItemEqual(playerIn.getHeldItem(hand), new ItemStack(Blocks.CONCRETE_POWDER), false, false) && handler.getFluid() != null && handler.getFluid().getFluid() == FluidRegistry.WATER)
            {
                ItemStack stackinhand = playerIn.getHeldItem(hand);
                ItemStack out = new ItemStack(Blocks.CONCRETE, 1, stackinhand.getItemDamage());
    
                playerIn.getHeldItem(hand).shrink(1);
                if (!worldIn.isRemote)
                {
                    worldIn.spawnEntity(new EntityItem(worldIn, playerIn.posX, playerIn.posY, playerIn.posZ, out));
                }
                return true;
            }
            else
            {
                if(!playerIn.isSneaking())
                    openGui(playerIn, (AdvancedTileEntity) worldIn.getTileEntity(pos));
                return true;
            }
        }
        return false;
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY, GuiContainer gui, int guiLeft, int guiTop)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY, gui, guiLeft, guiTop);
        int amount = 0;
        String name = "Empty";
        
        if (tank.getFluid() != null)
        {
            amount = tank.getFluidAmount();
            name = tank.getFluid().getFluid().getName();
        }
        
        getBuilder().drawString(gui, "Quantum Tank", 56, 8);
        
        getBuilder().drawBigBlueBar((AdvancedGui) gui, 30, 50, amount, tank.getCapacity(), mouseX - guiLeft, mouseY - guiTop, "", "Fluid Type: " + name, this.getBuilder().formatQuantityExact(amount) + " mb " + name);
    }
    
    @Override
    public Block getBlock()
    {
        return ModBlocks.TANK;
    }
    
    @Override
    public void addRecipe()
    {
        if (!ConfigQuantumStorage.disableQuantumTank)
        {
            RebornCraftingHelper.addShapedOreRecipe(new ItemStack(ModBlocks.TANK),
                    "OOO",
                    "IBI",
                    "III",
                    'I', new ItemStack(Items.IRON_INGOT),
                    'O', new ItemStack(Blocks.OBSIDIAN),
                    'B', new ItemStack(Items.BUCKET));
            
            RebornCraftingHelper.addShapelessRecipe(new ItemStack(ModBlocks.TANK), new ItemStack(ModBlocks.TANK));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound = super.writeToNBT(compound);
        tank.writeToNBT(compound); //porkman was here: no need to merge() with the returned tag, as they're the same tag lmfao

        return compound;
    }
    
    @Override
    public void writeToNBTWithoutCoords(NBTTagCompound tagCompound)
    {
        super.writeToNBTWithoutCoords(tagCompound);
        tank.writeToNBT(tagCompound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        tank.readFromNBT(compound);
        this.infiniteWater = this.getTileData().getBoolean("infin_water");
    }
    
    @Override
    public void readFromNBTWithoutCoords(NBTTagCompound compound)
    {
        super.readFromNBTWithoutCoords(compound);

        tank.readFromNBT(compound);
        this.infiniteWater = this.getTileData().getBoolean("infin_water");
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return true;
        }
        return false;
    }
    
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(tank);
        }
        return null;
    }

    @Override
    public void update()
    {
        handleUpgrades();
        if (!this.world.isRemote) {
            pushFluid(world, getPos(), tank, EnumFacing.UP);
            pushFluid(world, getPos(), tank, EnumFacing.DOWN);
        }
        this.trySync();
    }

    public void trySync() {
        if (this.tank.requestUpdate) {
            this.tank.requestUpdate = false;
            super.sync();
        }
    }

    @Override
    protected boolean pollSyncAllowed(boolean hasWatcher) {
        if (!super.pollSyncAllowed(hasWatcher)) {
            return false;
        }

        FluidStack currentStack = this.tank.getFluid();
        int currentAmount = this.tank.getFluidAmount();
        if (currentStack != this.lastSentStack
            || (currentAmount != this.lastSentAmount && (hasWatcher || isFluidAmountChangedEnoughToTriggerSync(currentAmount, this.lastSentAmount)))) {
            this.lastSentStack = currentStack;
            this.lastSentAmount = currentAmount;
            return true;
        }

        return false;
    }

    private static boolean isFluidAmountChangedEnoughToTriggerSync(int currentAmount, int lastSentAmount) {
        return Math.abs(currentAmount - lastSentAmount) >= Integer.MAX_VALUE  / 256;
    }

    private static FluidStack pushFluid(World world, BlockPos pos, IFluidHandler fluid, EnumFacing side)
    {
        try
        {
            if (!world.isRemote)
            {
                TileEntity tile = world.getTileEntity(pos.offset(side));
                if (tile != null && !(tile instanceof TileQuantumTank))
                {
                    IFluidHandler other = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
                    if (other != null)
                    {
                        return fluid.drain(other.fill(fluid.drain(1000, false), true), true);
                    }
                }
            }
        } catch (Exception e) {}
        return null;
    }
    
    public void handleUpgrades()
    {
        if (this.tank.isInfiniteWater() && this.tank.getFluidAmount() < this.tank.getCapacity())
        {
            this.tank.fill(tank.getFluid(), true);
        }
    }
    
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced)
    {
        if (stack != null && stack.hasTagCompound())
        {
            if (stack.getTagCompound().getCompoundTag("tileEntity") != null)
            {
                String fluidname = stack.getTagCompound().getCompoundTag("tileEntity").getString("FluidName");
                int fluidamount = stack.getTagCompound().getCompoundTag("tileEntity").getInteger("Amount");
                
                if (fluidamount != 0)
                {
                    tooltip.add(TextFormatting.GOLD + "Stored Fluid type: " + fluidamount + "mb " + fluidname);
                }

                NBTTagCompound forgeDataTag = stack.getTagCompound().getCompoundTag("tileEntity").getCompoundTag("ForgeData");
                if (forgeDataTag != null && forgeDataTag.getBoolean("infin_water")) {
                    tooltip.add(TextFormatting.DARK_PURPLE + "+ Infinite Water");
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean canRenderInLayer(IBlockState state, BlockRenderLayer layer) {
        return layer == BlockRenderLayer.CUTOUT_MIPPED || layer == BlockRenderLayer.TRANSLUCENT;
    }

    private final class EnhancedFluidTank extends FluidTank {
        private transient boolean requestUpdate;

        public EnhancedFluidTank(int capacity) {
            super(capacity);
        }

        private boolean isInfiniteWater() {
            return TileQuantumTank.this.infiniteWater && this.getFluid() != null && this.getFluid().getFluid() == FluidRegistry.WATER;
        }

        @Override
        protected void onContentsChanged() {
            super.onContentsChanged();

            this.requestUpdate = true;
        }
    }
}
