package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

public class GuiGunsFailed extends GuiScreen {
    private String title;
    private String message;

    public GuiGunsFailed(String title, String message) {
        this.title = "";
        this.message = "§cFailed to connect to mod host! §eFor details on why this happened please contact the client owner";
    }

    public GuiGunsFailed(){}

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.done")));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiMainMenu());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, "§cFailed to connect to mod host! §eFor details on why this happened please contact the client owner", this.width / 2, this.height / 2 - 30, 0xFF0000);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}