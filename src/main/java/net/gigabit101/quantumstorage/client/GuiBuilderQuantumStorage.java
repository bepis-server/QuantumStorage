package net.gigabit101.quantumstorage.client;

import com.mojang.blaze3d.platform.GlStateManager;
import net.gigabit101.quantumstorage.QuantumStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gigabit101 on 28/03/2017.
 */
public class GuiBuilderQuantumStorage
{
    public static final ResourceLocation GUI_SHEET = new ResourceLocation(QuantumStorage.MOD_ID.toLowerCase() + ":" + "textures/gui/gui_sheet.png");

    public void drawDefaultBackground(ContainerScreen gui, int x, int y, int width, int height) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft.getInstance().getTextureManager().bindTexture(GUI_SHEET);

        gui.blit(x, y, 0, 0, width / 2, height / 2);
        gui.blit(x + width / 2, y, 150 - width / 2, 0, width / 2, height / 2);
        gui.blit(x, y + height / 2, 0, 150 - height / 2, width / 2, height / 2);
        gui.blit(x + width / 2, y + height / 2, 150 - width / 2, 150 - height / 2, width / 2, height / 2);
    }

    public void drawPlayerSlots(ContainerScreen gui, int posX, int posY, boolean center)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(GUI_SHEET);
        if (center)
        {
            posX -= 81;
        }
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                gui.blit(posX + x * 18, posY + y * 18, 150, 0, 18, 18);
            }
        }
        for (int x = 0; x < 9; x++)
        {
            gui.blit(posX + x * 18, posY + 58, 150, 0, 18, 18);
        }
    }

    public void drawSlot(ContainerScreen gui, int posX, int posY)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(GUI_SHEET);
        gui.blit(posX, posY, 150, 0, 18, 18);
    }

    public void drawBigBlueBar(ContainerScreen gui, int x, int y, int value, int max, int mouseX, int mouseY, String suffix, String line2, String format)
    {
        Minecraft.getInstance().getTextureManager().bindTexture(GUI_SHEET);
        if (!suffix.equals(""))
        {
            suffix = " " + suffix;
        }
        gui.blit(x, y, 0, 218, 114, 18);
        int j = (int) ((double) value / (double) max * 106);
        if (j < 0)
            j = 0;
        gui.blit(x + 4, y + 4, 0, 236, j, 10);
        gui.drawString(Minecraft.getInstance().fontRenderer, format + suffix, x + 28, y + 5, 0xFFFFFF);
        if (isInRect(x, y, 114, 18, mouseX, mouseY))
        {
            int percentage = percentage(max, value);
            List<String> list = new ArrayList<>();
            list.add("" + TextFormatting.GOLD + value + "/" + max + suffix);
            list.add(getPercentageColour(percentage) + "" + percentage + "%" + TextFormatting.GRAY + " Full");
            list.add(line2);

            if (value > max)
            {
                list.add(TextFormatting.GRAY + "Yo this is storing more than it should be able to");
                list.add(TextFormatting.GRAY + "prolly a bug");
                list.add(TextFormatting.GRAY + "pls report and tell how tf you did this");
            }
            net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, mouseX, mouseY, gui.width, gui.height, -1, Minecraft.getInstance().fontRenderer);
            GlStateManager.disableLighting();
            GlStateManager.color4f(1, 1, 1, 1);
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
//
//    public void drawProgressBar(GuiContainer gui, int progress, int maxProgress, int x, int y, int mouseX, int mouseY)
//    {
//        gui.mc.getTextureManager().bindTexture(GUI_SHEET);
//        gui.drawTexturedModalRect(x, y, 150, 20, 17, 16);
//
//        int j = (int) ((double) progress / (double) maxProgress * 24);
//        if (j < 0)
//            j = 0;
//        gui.drawTexturedModalRect(x, y, 166, 20, j, 16);
//
//        if (isInRect(x, y, 26, 5, mouseX, mouseY))
//        {
//            int percentage = percentage(maxProgress, progress);
//            List<String> list = new ArrayList<>();
//            list.add(getPercentageColour(percentage) + "" + percentage + "%");
//            GuiUtils.drawHoveringText(list, mouseX, mouseY, gui.width, gui.height, -1, gui.mc.fontRenderer);
//            GlStateManager.disableLighting();
//            GlStateManager.color(1, 1, 1, 1);
//        }
//    }
//
//    public void drawTankWithOverlay(GuiScreen gui, FluidTank tank, int x, int y, float zLevel, int width, int height, int mouseX, int mouseY)
//    {
//        gui.mc.getTextureManager().bindTexture(GUI_SHEET);
//        gui.drawTexturedModalRect(x, y, 229, 18, width, height);
//
//
//        RenderUtil.renderGuiTank(tank, x + 2, y - 3, zLevel, width - 4, height);
//        if (isInRect(x, y, 14, height, mouseX, mouseY))
//        {
//            List<String> list = new ArrayList<String>();
//            if (tank.getFluid() != null)
//            {
//                list.add(tank.getFluidAmount() + " / " + tank.getCapacity() + " " + tank.getFluid().getLocalizedName());
//            } else
//            {
//                list.add("empty");
//            }
//            net.minecraftforge.fml.client.config.GuiUtils.drawHoveringText(list, mouseX, mouseY, gui.width, gui.height, -1, gui.mc.fontRenderer);
//            GlStateManager.disableLighting();
//        }
//    }

    public boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY)
    {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }
}