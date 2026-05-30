package net.minecraft.block;

import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockGrassPath extends Block {
    protected BlockGrassPath() {
        super(Material.ground);
        this.setLightOpacity(255);
    }

    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos,
            EnumFacing side) {
        switch (side) {
            case UP:
                return true;

            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                Block block = blockAccess.getBlockState(pos).getBlock();

            default:
                return super.shouldSideBeRendered(blockAccess, pos, side);
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos blockpos, IBlockState iblockstate) {
        this.setBlockBoundsBasedOnState(world, blockpos);
        return super.getCollisionBoundingBox(world, blockpos, iblockstate);
    }

    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos blockpos) {
        this.setBlockBoundsBasedOnState(world, blockpos);
        return super.getSelectedBoundingBox(world, blockpos);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, BlockPos blockpos) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for
     * render
     */
    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    @Nullable

    /**
     * Get the Item that this Block should drop when harvested.
     */
    public Item getItemDropped(IBlockState state, EaglercraftRandom rand, int fortune) {
        return Blocks.dirt.getItemDropped(
                Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT), rand, fortune);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(this);
    }

    public void onNeighborBlockChange(World world, BlockPos blockpos, IBlockState iblockstate, Block var4) {
        super.onNeighborBlockChange(world, blockpos, iblockstate, var4);

        if (world.getBlockState(blockpos.up()).getBlock().getMaterial().isSolid()) {
            world.setBlockState(blockpos, Blocks.dirt.getDefaultState());
        }
    }
}
