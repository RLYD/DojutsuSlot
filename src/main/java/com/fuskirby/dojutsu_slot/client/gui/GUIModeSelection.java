package com.fuskirby.dojutsu_slot.client.gui;

import com.fuskirby.dojutsu_slot.DojutsuSlot;
import com.fuskirby.dojutsu_slot.enums.WorldMode;
import com.fuskirby.dojutsu_slot.network.packet.PacketModeSelected;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import javax.annotation.ParametersAreNonnullByDefault;

public class GUIModeSelection extends GuiScreen {
    private GuiButton classicButton;
    private GuiButton dojutsuButton;

    @Override
    public void initGui() {
        int centerX = width / 2;
        int centerY = height / 2;

        classicButton = addButton(new GuiButton(1, centerX - 100, centerY - 30, 200, 20,
                I18n.format("gui.dojutsu_slot.mode.classic")));
        dojutsuButton = addButton(new GuiButton(2, centerX - 100, centerY, 200, 20,
                I18n.format("gui.dojutsu_slot.mode.dojutsu")));
    }

    @Override
    @ParametersAreNonnullByDefault
    protected void actionPerformed(GuiButton button) {
        if (button == classicButton) {
            selectMode(WorldMode.CLASSIC);
        } else if (button == dojutsuButton) {
            selectMode(WorldMode.DOJUTSU);
        }
    }

    private void selectMode(WorldMode mode) {
        DojutsuSlot.network.sendToServer(new PacketModeSelected(mode));
        mc.displayGuiScreen(null);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        drawCenteredString(fontRenderer, I18n.format("gui.dojutsu_slot.mode.title"), width / 2, height / 2 - 60, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
