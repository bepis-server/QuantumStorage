package QuantumStorage.client;

import QuantumStorage.QuantumStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.client.config.GuiUtils;
import reborncore.client.RenderUtil;
import reborncore.client.guibuilder.GuiBuilder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gigabit101 on 28/03/2017.
 */
public class GuiBuilderQuantumStorage extends GuiBuilder
{
    public static final ResourceLocation GUI_SHEET = new ResourceLocation(QuantumStorage.MOD_ID.toLowerCase() + ":" + "textures/gui/gui_sheet.png");
    
    public GuiBuilderQuantumStorage()
    {
        super(GUI_SHEET);
    }

    public String formatQuantityApprox(int value) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getJavaLocale());
        numberFormat.setMaximumFractionDigits(1);
        if (value >= 1_000_000_000) {
            return numberFormat.format(value / 1_000_000_000d) + 'B';
        } else if (value >= 1_000_000) {
            return numberFormat.format(value / 1_000_000d) + 'M';
        } else if (value >= 1_000) {
            return numberFormat.format(value / 1_000d) + 'K';
        } else {
            return numberFormat.format(value);
        }
    }

    public String formatQuantityExact(int value) {
        return NumberFormat.getIntegerInstance(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getJavaLocale()).format(value);
    }
    
    public void drawBigBlueBar(AdvancedGui gui, int x, int y, int value, int max, int mouseX, int mouseY, String suffix, String line2, String format)
    {
        gui.mc.getTextureManager().bindTexture(GUI_SHEET);
        if (!suffix.equals(""))
        {
            suffix = " " + suffix;
        }
        gui.drawTexturedModalRect(x, y, 0, 218, 114, 18);
        int j = (int) ((double) value / (double) max * 106);
        if (j < 0)
            j = 0;
        gui.drawTexturedModalRect(x + 4, y + 4, 0, 236, j, 10);
        gui.drawCentredString(format + suffix, y + 5, 0xFFFFFF);
        if (isInRect(x, y, 114, 18, mouseX, mouseY))
        {
            int percentage = percentage(max, value);
            List<String> list = new ArrayList<>();
            list.add("" + TextFormatting.GOLD + this.formatQuantityExact(value) + "/" + this.formatQuantityExact(max) + suffix);
            list.add(getPercentageColour(percentage) + "" + percentage + "%" + TextFormatting.GRAY + " Full");
            list.add(line2);
            
            if (value > max)
            {
                list.add(TextFormatting.GRAY + "Yo this is storing more than it should be able to");
                list.add(TextFormatting.GRAY + "prolly a bug");
                list.add(TextFormatting.GRAY + "pls report and tell how tf you did this");
            }
            net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, mouseX, mouseY, gui.width, gui.height, -1, gui.mc.fontRenderer);
            GlStateManager.disableLighting();
            GlStateManager.color(1, 1, 1, 1);
        }
    }
    
    public TextFormatting getPercentageColour(int percentage)
    {
        if (percentage <= 10)
        {
            return TextFormatting.RED;
        } else if (percentage >= 75)
        {
            return TextFormatting.GREEN;
        } else
        {
            return TextFormatting.YELLOW;
        }
    }
    
    public int percentage(int MaxValue, int CurrentValue)
    {
        if (CurrentValue == 0)
            return 0;
        return (int) ((CurrentValue * 100.0f) / MaxValue);
    }
    
    public void drawProgressBar(GuiContainer gui, int progress, int maxProgress, int x, int y, int mouseX, int mouseY)
    {
        gui.mc.getTextureManager().bindTexture(GUI_SHEET);
        gui.drawTexturedModalRect(x, y, 150, 20, 17, 16);
        
        int j = (int) ((double) progress / (double) maxProgress * 24);
        if (j < 0)
            j = 0;
        gui.drawTexturedModalRect(x, y, 166, 20, j, 16);
        
        if (isInRect(x, y, 26, 5, mouseX, mouseY))
        {
            int percentage = percentage(maxProgress, progress);
            List<String> list = new ArrayList<>();
            list.add(getPercentageColour(percentage) + "" + percentage + "%");
            GuiUtils.drawHoveringText(list, mouseX, mouseY, gui.width, gui.height, -1, gui.mc.fontRenderer);
            GlStateManager.disableLighting();
            GlStateManager.color(1, 1, 1, 1);
        }
    }
    
    public void drawTankWithOverlay(GuiScreen gui, FluidTank tank, int x, int y, float zLevel, int width, int height, int mouseX, int mouseY)
    {
        gui.mc.getTextureManager().bindTexture(GUI_SHEET);
        gui.drawTexturedModalRect(x, y, 229, 18, width, height);
        
        
        RenderUtil.renderGuiTank(tank, x + 2, y - 3, zLevel, width - 4, height);
        if (isInRect(x, y, 14, height, mouseX, mouseY))
        {
            List<String> list = new ArrayList<String>();
            if (tank.getFluid() != null)
            {
                list.add(tank.getFluidAmount() + " / " + tank.getCapacity() + " " + tank.getFluid().getLocalizedName());
            } else
            {
                list.add("empty");
            }
            net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, mouseX, mouseY, gui.width, gui.height, -1, gui.mc.fontRenderer);
            GlStateManager.disableLighting();
        }
    }
    
    public boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY)
    {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }
}
