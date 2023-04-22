package QuantumStorage.items;

import QuantumStorage.GuiHandler;
import QuantumStorage.QuantumStorage;
import QuantumStorage.api.IColorable;
import QuantumStorage.items.prefab.ItemBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ItemQuantumBag extends ItemBase implements IColorable
{
    public static final String[] COLOURS = new String[]{"white", "orange", "magenta", "lightBlue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"};
    
    public ItemQuantumBag()
    {
        setTranslationKey(QuantumStorage.MOD_ID + ".quantum_bag");
        setRegistryName("quantum_bag");
        setMaxStackSize(1);
    }
    
    @Override
    public String getTranslationKey(ItemStack itemStack)
    {
        int meta = itemStack.getItemDamage();
        if (meta < 0 || meta >= COLOURS.length)
        {
            meta = 0;
        }
        return super.getTranslationKey() + "." + COLOURS[meta];
    }
    
    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
    {
        if (this.isInCreativeTab(tab))
        {
            for (int meta = 0; meta < COLOURS.length; ++meta)
            {
                items.add(new ItemStack(this, 1, meta));
            }
        }
    }
    
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        if (!worldIn.isRemote)
        {
            playerIn.openGui(QuantumStorage.INSTANCE, GuiHandler.BAG_ID, worldIn, 0, 0, 0);
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
    
    @Override
    public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
    {
        if (par1ItemStack.getItemDamage() >= EnumDyeColor.values().length)
        {
            return 0xFFFFFF;
        }
        return EnumDyeColor.byMetadata(par1ItemStack.getItemDamage()).getColorValue();
    }
}
