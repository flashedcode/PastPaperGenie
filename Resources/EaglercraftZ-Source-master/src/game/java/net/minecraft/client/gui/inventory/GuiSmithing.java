package net.minecraft.client.gui.inventory;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.client.inventory.ContainerSmithing;

public class GuiSmithing extends GuiContainer {
	private static final ResourceLocation SMITHING_GUI_TEXTURE = new ResourceLocation(
			"textures/gui/container/smithing.png");

	public GuiSmithing(InventoryPlayer inventoryplayer) {
		super(new ContainerSmithing(inventoryplayer));
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.mc.getTextureManager().bindTexture(SMITHING_GUI_TEXTURE);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		if ((this.inventorySlots.getSlot(0).getHasStack() || this.inventorySlots.getSlot(1).getHasStack())
				&& !((ContainerSmithing) this.inventorySlots).isCraftingValid()) {
			this.drawTexturedModalRect(k + 99, l + 45, 176, 0, 28, 21);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		String text = "Upgrade Gear";
		this.fontRendererObj.drawString(text, ((this.xSize - this.fontRendererObj.getStringWidth(text)) / 2) + 7, 18,
				0xFFFFFF);
	}
}