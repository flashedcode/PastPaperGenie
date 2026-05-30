package net.minecraft.client.gui;

//import net.minecraft.client.gui.GuiScreen;
//import net.minecraft.client.gui.GuiButton;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenLANInfo;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenLANNotSupported;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiShareToLan;
import net.lax1dude.eaglercraft.v1_8.sp.lan.LANServerController;
import net.lax1dude.eaglercraft.v1_8.update.GuiUpdateCheckerOverlay;
import net.lax1dude.eaglercraft.v1_8.voice.GuiVoiceMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class GuiRealms extends GuiScreen {

    //@Override
    public void image() {
    	ResourceLocation image = new ResourceLocation("eagler", "eaglerz/gui/eaglercraftz.png");
	    Minecraft.getMinecraft().getTextureManager().bindTexture(image);

		// Coordinates where you want to draw the image
		int imageWidth = 128;
		int imageHeight = 16;

		// Image width and height
		int x = (int) ((this.width - imageWidth) / 2);
		int y = (int) ((this.height - imageHeight) / 3.2);

		// Draw the texture (image)
		this.drawModalRectWithCustomSizedTexture(x, y, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
    }

    @Override
    public void initGui() {

        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 - 5, "Close"));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 - 30, "Discord"));
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            this.mc.displayGuiScreen(null); // This closes the GUI
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground(); // Draw default background
        this.drawCenteredString(this.fontRendererObj, "Connecting to services...", this.width / 2, 20, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, "§4Failed to connect! Please try again later", this.width / 2, 35, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return true; // If true, game pauses when the GUI is open
    }
}
