package com.fuskirby.dojutsu_slot.client.gui;

import java.io.IOException;
import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.ModConfigs;
import com.fuskirby.dojutsu_slot.inventory.ContainerDojutsuSlot;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonImage;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.recipebook.GuiRecipeBook;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

public class GuiDojutsuSlotInventory extends InventoryEffectRenderer implements IRecipeShownListener
{
    public ResourceLocation texture;

    public float oldMouseX;
    public float oldMouseY;
    private GuiButtonImage recipeBookButton;
    private final GuiRecipeBook recipeBook = new GuiRecipeBook()
    {
        private boolean bypass = false;

        @Override
        public boolean isVisible()
        {
            if (bypass)
                return super.isVisible();

            return super.isVisible() && !ModConfigs.DojutsuSlotDisableRecipeBook;
        }

        @Override
        public void toggleVisibility()
        {
            boolean last = bypass;

            bypass = true;
            super.toggleVisibility();
            bypass = last;
        }
    };
    private boolean widthTooNarrow;
    private boolean buttonClicked;

    public GuiDojutsuSlotInventory(Container container)
    {
        super(container);
        allowUserInput = true;
        this.texture = getBackgroundTexture(container);
    }

    private ResourceLocation getBackgroundTexture(Container container) {
        if (container instanceof ContainerDojutsuSlot) {
            ContainerDojutsuSlot dojutsuslotContainer = (ContainerDojutsuSlot) container;
            if (dojutsuslotContainer.getDojutsuSlotCount() == 4) {
                return new ResourceLocation("dojutsu_slot", "textures/gui/dojutsuslotinventory_4slot.png");
            }
        }
        return new ResourceLocation("dojutsu_slot", "textures/gui/dojutsuslotinventory.png");
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 10)
        {
            recipeBook.initVisuals(widthTooNarrow, ((ContainerDojutsuSlot) inventorySlots).craftMatrix);
            recipeBook.toggleVisibility();
            guiLeft = recipeBook.updateScreenPosition(widthTooNarrow, width, xSize);
            recipeBookButton.setPosition(guiLeft + 76, guiTop + 27);

            buttonClicked = true;
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(this.texture);
        int i = guiLeft;
        int j = guiTop;
        drawTexturedModalRect(i, j, 0, 0, xSize, ySize);

        GuiInventory.drawEntityOnScreen(i + 51, j + 75, 30, i + 51 - oldMouseX, j + 75 - 50 - oldMouseY, mc.player);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        fontRenderer.drawString(I18n.format("container.crafting"), 97, 8, 4210752);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();

        hasActivePotionEffects = !recipeBook.isVisible();

        if (recipeBook.isVisible() && widthTooNarrow)
        {
            drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
            recipeBook.render(mouseX, mouseY, partialTicks);
        }
        else
        {
            recipeBook.render(mouseX, mouseY, partialTicks);
            super.drawScreen(mouseX, mouseY, partialTicks);
            recipeBook.renderGhostRecipe(guiLeft, guiTop, false, partialTicks);
        }

        renderHoveredToolTip(mouseX, mouseY);
        recipeBook.renderTooltip(guiLeft, guiTop, mouseX, mouseY);

        oldMouseX = mouseX;
        oldMouseY = mouseY;
    }

    @Override
    @MethodsReturnNonnullByDefault
    public GuiRecipeBook func_194310_f()
    {
        return recipeBook;
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type)
    {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        recipeBook.slotClicked(slotIn);
    }

    @Override
    protected boolean hasClickedOutside(int x, int y, int guiLeft, int guiTop)
    {
        boolean flag = x < guiLeft || y < guiTop || x >= guiLeft + xSize || y >= guiTop + ySize;
        return recipeBook.hasClickedOutside(x, y, this.guiLeft, this.guiTop, xSize, ySize) && flag;
    }

    @Override
    public void initGui()
    {
        buttonList.clear();
        super.initGui();

        widthTooNarrow = width < 379;
        recipeBook.func_194303_a(width, height, mc, widthTooNarrow, ((ContainerDojutsuSlot) inventorySlots).craftMatrix);
        guiLeft = recipeBook.updateScreenPosition(widthTooNarrow, width, xSize);

        recipeBookButton = new GuiButtonImage(10, guiLeft + 76, guiTop + 27, 20, 18, 178, 0, 19, INVENTORY_BACKGROUND);
        if (!ModConfigs.DojutsuSlotDisableRecipeBook)
        {
            buttonList.add(recipeBookButton);
        }
    }

    @Override
    protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY)
    {
        return (!widthTooNarrow || !recipeBook.isVisible()) && super.isPointInRegion(rectX, rectY, rectWidth, rectHeight, pointX, pointY);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (!recipeBook.keyPressed(typedChar, keyCode))
        {
            if (keyCode == DojutsuSlot.keyHandler.keyOpenDojutsuSlotInventory.getKeyCode())
                mc.player.closeScreen();
            else
                super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int x, int y, int button) throws IOException
    {
        if (!recipeBook.mouseClicked(x, y, button))
            if (!widthTooNarrow || recipeBook.isVisible())
                super.mouseClicked(x, y, button);
    }

    @Override
    protected void mouseReleased(int x, int y, int state)
    {
        if (buttonClicked)
            buttonClicked = false;
        else
            super.mouseReleased(x, y, state);
    }

    @Override
    public void onGuiClosed()
    {
        recipeBook.removed();
        super.onGuiClosed();
    }

    @Override
    public void recipesUpdated()
    {
        recipeBook.recipesUpdated();
    }

    @Override
    public void updateScreen()
    {
        recipeBook.tick();
    }
}