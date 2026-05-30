package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;

public class BlockEndRod extends Block {
    public BlockEndRod() {
        super(Material.circuits); // Use a material that fits the End Rod's behavior
//        this.setBlockName("end_rod"); // Internal name
//        this.setBlockTextureName("end_rod"); // Texture name
        this.setHardness(0.0F); // Hardness of 0.0 (breaks instantly)
        this.setStepSound(Block.soundTypeWood); // Sound when walking on it
        this.setCreativeTab(CreativeTabs.tabDecorations); // Add to the Decorations tab
    }

    @Override
    public boolean isOpaqueCube() {
        return false; // Make the block non-opaque (transparent)
    }

//    @Override
    public boolean renderAsNormalBlock() {
        return false; // Render as a custom block
    }

    @Override
    public int getRenderType() {
        return 1; // Use a custom render type
    }

//    @Override
//    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
//        return world.getBlock(x, y - 1, z).isOpaqueCube(); // Ensure it can only be placed on solid blocks
    //}
}