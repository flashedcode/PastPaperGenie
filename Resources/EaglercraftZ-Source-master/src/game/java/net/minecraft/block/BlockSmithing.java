package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraft.client.gui.inventory.GuiSmithing;
import net.minecraft.client.inventory.ContainerSmithing;

public class BlockSmithing extends Block {
	public class InterfaceSmithing implements IInteractionObject {
		public InterfaceSmithing() {
		}

		@Override
		public ContainerSmithing createContainer(InventoryPlayer inventoryplayer, EntityPlayer var2) {
			return new ContainerSmithing(inventoryplayer);
		}

		@Override
		public IChatComponent getDisplayName() {
			return new ChatComponentTranslation(BlockSmithing.this.getUnlocalizedName() + ".name", new Object[0]);
		}

		@Override
		public String getGuiID() {
			return "smithing_table";
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public boolean hasCustomName() {
			return false;
		}
	}

	public BlockSmithing() {
		super(Material.wood);
		this.setHardness(2.5F);
		this.setStepSound(soundTypeWood);
		this.setCreativeTab(CreativeTabs.tabDecorations);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos var2, IBlockState var3, EntityPlayer entityplayer,
			EnumFacing var5, float var6, float var7, float var8) {
		if (!world.isRemote) {
			entityplayer.displayGui(new BlockSmithing.InterfaceSmithing());
		}
		return true;
	}
}