package net.minecraft.client.gui;

import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.gui.GuiSlider;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.util.IChatComponent;
import net.minecraft.client.gui.*;

import net.minecraft.client.resources.I18n;

// TODO: make this work on touchscreens

public class GuiAccessibility extends GuiScreen {
    private final GuiScreen parentScreen;
    private String title;
    private GameSettings options;

    private GuiSlider textBackgroundOpacitySlider;
    private GuiSlider chatTextOpacitySlider;

    public GuiAccessibility(GuiScreen parentScreenIn, GameSettings settings) {
        this.parentScreen = parentScreenIn;
        this.options = settings;
    }

    /**
     * +
     * Adds the buttons (and other controls) to the screen in
     * question. Called when the GUI is displayed and when the
     * window resizes, the buttonList is cleared beforehand.
     */
    public void initGui() {
        //int i = 0;
        this.title = "Accessibility Settings...";
        GuiButton testButton = new GuiButton(1, this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, "Narrator: OFF");
                testButton.enabled = false; // Disable the button
                this.buttonList.add(testButton);
        GuiButton test1Button = new GuiButton(2, this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, "Show Subtitles: OFF");
                test1Button.enabled = false; // Disable the button
                this.buttonList.add(test1Button);
       // this.textBackgroundOpacitySlider = new GuiSlider(100, this.width / 2 - 155, this.height / 6 + 72, 150, 20, "Text BG Opacity: ", "%", 0.0F, 100.0F, 58.0F, false, true);
        //this.textBackgroundOpacitySlider.enabled = false; // Disable the slider
        //this.buttonList.add(this.textBackgroundOpacitySlider);
        GuiButton test2Button = new GuiButton(3, this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, "Text Backrgound: Chat");
                test2Button.enabled = true; // Disable the button
                this.buttonList.add(test2Button);
        GuiButton test3Button = new GuiButton(4, this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, "Auto-Jump: OFF");
                test3Button.enabled = false; // Disable the button
                this.buttonList.add(test3Button);
        GuiButton test4Button = new GuiButton(5, this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, "Hide Password: OFF");
                test4Button.enabled = false; // Disable the button
                this.buttonList.add(test4Button);
        GuiButton test5Button = new GuiButton(6, this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, "Guns Mod: OFF");
                test5Button.enabled = true; // Disable the button
                this.buttonList.add(test5Button);
        this.buttonList.add(new GuiButton(7, this.width / 2 - 100, this.height / 6 + 168,
				I18n.format("gui.done", new Object[0])));
        //this.buttonList.add(new GuiButton(GameSettings.Options.HIDE_PASSWORD.returnEnumOrdinal(),
                //this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20,
                //this.options.getKeyBinding(GameSettings.Options.HIDE_PASSWORD)));
        //++i;
        //this.buttonList.add(new GuiButton(GameSettings.Options.ENABLE_SOUND.returnEnumOrdinal(),
                //this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20,
                //this.options.getKeyBinding(GameSettings.Options.ENABLE_SOUND)));
        //++i;
        //this.buttonList.add(new GuiButton(GameSettings.Options.DISABLE_ALPHA.returnEnumOrdinal(),
                //this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1), 150, 20,
                //this.options.getKeyBinding(GameSettings.Options.DISABLE_ALPHA)));
        //++i;
        //++i;
        //this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 24 * (i >> 1),
                //I18n.format("gui.done", new Object[0])));
    }

    protected void actionPerformed(GuiButton parGuiButton) {
        if (parGuiButton.enabled) {
            if (parGuiButton.id == 6) {
                this.mc.displayGuiScreen(new GuiWarningGuns());
            }
            if (parGuiButton.id == 7) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parentScreen);
            //}
            //if (parGuiButton.id == GameSettings.Options.HIDE_PASSWORD.returnEnumOrdinal()) {
                //this.options.setOptionValue(GameSettings.Options.HIDE_PASSWORD, 1);
                //parGuiButton.displayString = this.options.getKeyBinding(GameSettings.Options.HIDE_PASSWORD);
            //}

            //if (parGuiButton.id == GameSettings.Options.ENABLE_SOUND.returnEnumOrdinal()) {
                //this.options.setOptionValue(GameSettings.Options.ENABLE_SOUND, 1);
                //parGuiButton.displayString = this.options.getKeyBinding(GameSettings.Options.ENABLE_SOUND);
            //}

            //if (parGuiButton.id == GameSettings.Options.DISABLE_ALPHA.returnEnumOrdinal()) {
                //this.options.setOptionValue(GameSettings.Options.DISABLE_ALPHA, 1);
                //parGuiButton.displayString = this.options.getKeyBinding(GameSettings.Options.DISABLE_ALPHA);
            }
        }
    }

    public void drawScreen(int i, int j, float f) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
        super.drawScreen(i, j, f);
    }

}
