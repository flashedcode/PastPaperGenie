package net.minecraft.client.gui.crafting;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.crafting.RecipesEaglerZ;

public class SmithingManager {
	private static final SmithingManager instance = new SmithingManager();

	public static SmithingManager getInstance() {
		return instance;
	}

	private final Map<ItemStack, ItemStack> smithingList = new HashMap<>();

	public SmithingManager() {
		RecipesEaglerZ.addSmithingRecipes(this);
	}

	public void addSmithingRecipe(ItemStack input, ItemStack output) {
		if (getSmithingResult(input) != null) {
			return;
		}
		smithingList.put(input, output);
	}

	public ItemStack getSmithingResult(ItemStack input) {
		for (Map.Entry<ItemStack, ItemStack> entry : smithingList.entrySet()) {
			if (input.getItem() == entry.getKey().getItem()) {
				return entry.getValue();
			}
		}
		return null;
	}
}