package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;
import net.minecraft.block.properties.PropertyEnum;

/**
 * Custom BlockCherryLog for handling axis-based rotation
 */
public class BlockCherryLog extends Block {
    public static final PropertyEnum AXIS = PropertyEnum.create("axis", EnumAxis.class);

    public BlockCherryLog() {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, EnumAxis.Y));
    }

    public MapColor getMapColor(IBlockState state) {
        return MapColor.woodColor;
    }

    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(AXIS, EnumAxis.fromFacingAxis(facing.getAxis()));
    }

    protected BlockState createBlockState() {
        return new BlockState(this, AXIS);
    }

    public int getMetaFromState(IBlockState state) {
        EnumAxis axis = (EnumAxis) state.getValue(AXIS);
        return axis.ordinal();
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumAxis axis = EnumAxis.values()[meta];
        return this.getDefaultState().withProperty(AXIS, axis);
    }

    public enum EnumAxis implements IStringSerializable {
        X("x"),
        Y("y"),
        Z("z"),
        NONE("none");

        private final String name;

        EnumAxis(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return this.name;
        }

        public static EnumAxis fromFacingAxis(EnumFacing.Axis axis) {
            switch (axis) {
                case X:
                    return X;
                case Y:
                    return Y;
                case Z:
                    return Z;
                default:
                    return NONE;
            }
        }
    }
}
