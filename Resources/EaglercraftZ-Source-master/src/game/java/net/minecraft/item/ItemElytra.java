package net.minecraft.item;

import javax.annotation.Nullable;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemElytra extends Item {
    public ItemElytra() {
        this.maxStackSize = 1;
        this.setMaxDamage(432);
        this.setCreativeTab(CreativeTabs.tabTransport);
        // this.addPropertyOverride(new ResourceLocation("broken"), new IItemPropertyGetter() {
        //     public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
        //         return ItemElytra.isBroken(stack) ? 0.0F : 1.0F;
        //     }
        // });
    }

    public static boolean isBroken(ItemStack stack) {
        return stack.getItemDamage() < stack.getMaxDamage() - 1;
    }

    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair.getItem() == Items.leather;
    }

    public ItemStack onItemRightClick(ItemStack itemstack, World var2, EntityPlayer entityplayer) {
        int i = EntityLiving.getArmorPosition(itemstack) - 1;
        ItemStack itemstack1 = entityplayer.getCurrentArmor(i);
        if (itemstack1 == null) {
            entityplayer.setCurrentItemOrArmor(i, itemstack.copy());
            itemstack.stackSize = 0;
        }

        return itemstack;
    }
}
