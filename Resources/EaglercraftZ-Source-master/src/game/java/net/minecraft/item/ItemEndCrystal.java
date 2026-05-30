package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemEndCrystal extends Item {
    public ItemEndCrystal() {
        setUnlocalizedName("end_crystal");
        setCreativeTab(CreativeTabs.tabCombat);
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        // Only allow placement on obsidian or bedrock
        if (block != Blocks.obsidian && block != Blocks.bedrock) {
            return false;
        }

        if (!world.isRemote) {
            // Calculate placement position (above the clicked block)
            BlockPos crystalPos = pos.up();
            
            // Spawn the end crystal entity
            EntityEnderCrystal crystal = new EntityEnderCrystal(world);
            crystal.setPosition(crystalPos.getX() + 0.5, crystalPos.getY(), crystalPos.getZ() + 0.5);
            world.spawnEntityInWorld(crystal);
            
            // Consume the item
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return true;
    }
}