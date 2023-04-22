package QuantumStorage.upgrades;

import QuantumStorage.QuantumStorage;
import QuantumStorage.items.prefab.ItemBase;
import QuantumStorage.tiles.TileQuantumStorageUnit;
import QuantumStorage.tiles.TileQuantumTank;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Gigabit101 on 28/01/2017.
 */
public class ItemUpgrade extends ItemBase
{
    public static final String[] types = new String[]{"render", "void", "creative", "water"};
    
    public ItemUpgrade()
    {
        setTranslationKey(QuantumStorage.MOD_ID + ".upgrade");
        setRegistryName("upgrade");
        setHasSubtypes(true);
    }
    
    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileQuantumStorageUnit)
        {
            TileQuantumStorageUnit dsu = (TileQuantumStorageUnit) worldIn.getTileEntity(pos);
            int meta = player.getHeldItem(hand).getItemDamage();
            if (meta == 2)//Creative
            {
                if (!dsu.inv.getStackInSlot(2).isEmpty())
                {
                    dsu.inv.getStackInSlot(0).setCount(Integer.MAX_VALUE - 128);
                }
            }
        }
        int meta = player.getHeldItem(hand).getItemDamage();
        if (worldIn.getTileEntity(pos) != null && worldIn.getTileEntity(pos) instanceof TileQuantumTank)
        {
            TileQuantumTank tank = (TileQuantumTank) worldIn.getTileEntity(pos);
            
            if (meta == 3)//water
            {
                tank.getTileData().setBoolean("infin_water", true);
            }
        }
        return super.onItemUse(player, worldIn, pos, hand, facing, hitX, hitY, hitZ);
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> list)
    {
        if (!isInCreativeTab(tab))
        {
            return;
        }
        
        for (int meta = 0; meta < types.length; meta++)
        {
            list.add(new ItemStack(this, 1, meta));
        }
    }
    
    @Override
    public String getTranslationKey(ItemStack itemStack)
    {
        int meta = itemStack.getItemDamage();
        if (meta < 0 || meta >= types.length)
        {
            meta = 0;
        }
        return super.getTranslationKey() + "." + types[meta];
    }
    
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced)
    {
        tooltip.add(TextFormatting.DARK_PURPLE + I18n.format("tooltip" + getTranslationKey(stack)));
    }
}
