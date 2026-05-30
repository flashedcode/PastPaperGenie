package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.stats.StatList;

public class ItemTrident extends Item {
    public ItemTrident() {
        this.setUnlocalizedName("trident");
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setMaxStackSize(1);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            // Spawn the trident entity
            //world.spawnEntityInWorld(new EntityTrident(world, entityplayer));
        }
        return itemStack;
    }
}