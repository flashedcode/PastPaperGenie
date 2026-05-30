package net.minecraft.client.gui.crafting;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
//import net.starlikeclient.minecraft.blocks.BlockMosaic;
//import net.starlikeclient.minecraft.crafting.RecipesBackpackUpgrade;
import net.minecraft.client.gui.crafting.SmithingManager;;

public class RecipesEaglerZ {
	public static void addCraftingRecipes(CraftingManager e) {
		addIngotRecipe(e, Blocks.netherite_block, Items.netherite_ingot);
	}

	private static void addIngotRecipe(CraftingManager e, Block block, Item item) {
		ItemStack itemstack = new ItemStack(item, 9);
		e.addRecipe(new ItemStack(block), new Object[] { "###", "###", "###", Character.valueOf('#'), itemstack });
		e.addRecipe(itemstack, new Object[] { "#", Character.valueOf('#'), block });
	}

	public static void addSmithingRecipes(SmithingManager e) {
		e.addSmithingRecipe(new ItemStack(Items.diamond_sword), new ItemStack(Items.netherite_sword));
		e.addSmithingRecipe(new ItemStack(Items.diamond_pickaxe), new ItemStack(Items.netherite_pickaxe));
		e.addSmithingRecipe(new ItemStack(Items.diamond_axe), new ItemStack(Items.netherite_axe));
		e.addSmithingRecipe(new ItemStack(Items.diamond_shovel), new ItemStack(Items.netherite_shovel));
		e.addSmithingRecipe(new ItemStack(Items.diamond_hoe), new ItemStack(Items.netherite_hoe));
		e.addSmithingRecipe(new ItemStack(Items.diamond_helmet), new ItemStack(Items.netherite_helmet));
		e.addSmithingRecipe(new ItemStack(Items.diamond_chestplate), new ItemStack(Items.netherite_chestplate));
		e.addSmithingRecipe(new ItemStack(Items.diamond_leggings), new ItemStack(Items.netherite_leggings));
		e.addSmithingRecipe(new ItemStack(Items.diamond_boots), new ItemStack(Items.netherite_boots));
	}
}