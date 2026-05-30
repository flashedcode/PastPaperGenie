package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

public class GuiGunsLoading extends GuiScreen {
    private String title;
    private String message;

    public GuiGunsLoading(String title, String message) {
        this.title = "";
        this.message = "Connecting to Mod Host, Please Wait...";
    }

    public GuiGunsLoading(){}

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, 
        I18n.format("gui.cancel")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiGunsFailed());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, "Connecting to Mod Host, Please Wait...", this.width / 2, this.height / 2 - 30, 0xFF0000);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}