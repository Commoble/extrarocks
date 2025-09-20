package commoble.extrarocks.datagen;

import java.util.List;
import java.util.Map;

import net.minecraft.core.HolderSet;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.level.ItemLike;

public class RecipeHelpers
{
	public static ShapelessRecipe shapeless(ItemLike result, int count, CraftingBookCategory category, List<Ingredient> ingredients)
	{
		return new ShapelessRecipe(
			"", // recipe book group (not used)
			category,
			new ItemStack(result, count),
			NonNullList.copyOf(ingredients));
	}
	
	public static ShapedRecipe shaped(ItemLike result, int count, CraftingBookCategory category, List<String> pattern, Map<Character,Ingredient> key)
	{
		return new ShapedRecipe(
			"", // recipe book group (not used),
			category,
			ShapedRecipePattern.of(key, pattern),
			new ItemStack(result, count),
			true);
	}
	
	public static StonecutterRecipe stonecutting(ItemLike result, int count, Ingredient ingredient)
	{
		return new StonecutterRecipe("", ingredient, new ItemStack(result, count));
	}
	
	public static SmeltingRecipe smelting(ItemLike result, Ingredient input, float xp, CookingBookCategory category)
	{
		return new SmeltingRecipe(
			"",
			category,
			input,
			new ItemStack(result),
			xp,
			200);
	}
	
	public static BlastingRecipe blasting(ItemLike result, Ingredient input, float xp, CookingBookCategory category)
	{
		return new BlastingRecipe(
			"",
			category,
			input,
			new ItemStack(result),
			xp,
			100);
	}
	
	@SuppressWarnings("deprecation")
	public static Ingredient tagIngredient(TagKey<Item> tag)
	{
		return Ingredient.of(HolderSet.emptyNamed(BuiltInRegistries.ITEM, tag));
	}
}