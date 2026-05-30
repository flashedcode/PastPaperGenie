package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;
import net.minecraft.client.gui.*;

public class GuiWarningGuns extends GuiScreen {
    private String title;
    private String message;

    public GuiWarningGuns(String title, String message) {
        this.title = "";
        this.message = "§l§cWARNING: §r§ePlease note that you are violating Minecrafts's EULA and Microsoft's Terms Of Service. §cDo you wish to proceed?";
    }

    public GuiWarningGuns(){}

    @Override
    public void initGui() {
        //this.buttonList.add(new GuiButton(6, this.width / 2 - 100, this.height / 6 + 168,
        //I18n.format("gui.done", new Object[0])));
        //this.buttonList.add(optButton = new GuiButton(1, this.width / 2 - 100, this.height / 6 + 108, I18n.format("§a§lYes") + I18n.format(enableState ? "§c§lAre you sure?" : "gui.no")));
        GuiButton test1Button = new GuiButton(1, this.width / 2 - 100, this.height / 6 + 108, "§a§lYes");
        test1Button.enabled = true; // Disable the button
        this.buttonList.add(test1Button);
        GuiButton test2Button = new GuiButton(2, this.width / 2 - 100, this.height / 6 + 138, "§c§lNo");
        test2Button.enabled = true; // Disable the button
        this.buttonList.add(test2Button);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            this.mc.displayGuiScreen(new GuiGunsLoading());
        }
        if (button.id == 2) {
            this.mc.displayGuiScreen(new GuiMainMenu());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        //this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, this.height / 2 - 50, 0xFFFFFF);
        //this.drawCenteredString(this.fontRendererObj, "§c§lWARNING: §r§ePlease note that you are violating Minecraft's EULA and Microsoft's Terms Of Service. §cDo you wish to proceed?", this.width / 2, this.height / 2 - 30, 0xFF0000);
        this.drawCenteredString(this.fontRendererObj, I18n.format("§c§lWARNING"), this.width / 2, 50, 0xFF4444);
		this.drawCenteredString(this.fontRendererObj, I18n.format("§r§ePlease note that you are violating"), this.width / 2, 70, 16777215);
		this.drawCenteredString(this.fontRendererObj, I18n.format("§r§eMinecraft's EULA and Microsoft's Terms Of Service."), this.width / 2, 82, 16777215);
		this.drawCenteredString(this.fontRendererObj, I18n.format("§cDo you wish to proceed?"), this.width / 2, 94, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}