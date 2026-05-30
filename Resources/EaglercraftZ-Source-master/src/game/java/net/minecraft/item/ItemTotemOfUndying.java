package net.minecraft.item;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ItemTotemOfUndying extends Item {

    public ItemTotemOfUndying() {
        this.setMaxStackSize(1); // Set the stack size to 1 (like the Totem of Undying)
        this.setUnlocalizedName("totemOfUndying"); // Set the item's unlocalized name
       // this.setTextureName("minecraft:totem_of_undying"); // Set the item's texture
    }

    //@Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        // Prevent death and apply effects when the player is about to die
        if (player.getHealth() <= 0.0F && !world.isRemote) {
            player.setHealth(1.0F); // Set the player's health to 1
            player.clearActivePotions(); // Clear all active potion effects
            player.addPotionEffect(new PotionEffect(Potion.regeneration.id, 900, 1)); // Regeneration II for 45 seconds
            player.addPotionEffect(new PotionEffect(Potion.absorption.id, 100, 1)); // Absorption II for 5 seconds
            player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 800, 0)); // Fire Resistance I for 40 seconds

            // Play a sound effect (you can use a custom sound or an existing one)
            world.playSoundAtEntity(player, "mob.enderdragon.growl", 1.0F, 1.0F);

            // Spawn particles (optional)
            //for (int i = 0; i < 30; i++) {
                //world.spawnParticle("flame", player.posX + (world.rand.nextDouble() - 0.5) * 2.0,
                        //player.posY + world.rand.nextDouble() * 2.0,
                        //player.posZ + (world.rand.nextDouble() - 0.5) * 2.0,
                        //0, 0, 0);
            //}

            // Consume the totem
            stack.stackSize--;
            return true;
        }
        return false;
    }
}