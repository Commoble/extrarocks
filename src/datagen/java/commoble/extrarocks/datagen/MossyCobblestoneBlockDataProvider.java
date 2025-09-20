package commoble.extrarocks.datagen;

import java.util.List;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public class MossyCobblestoneBlockDataProvider extends PaletteSwappedBlockDataProvider
{
	protected Block cobbleBlock;

	public MossyCobblestoneBlockDataProvider(Block mossyCobbleBlock, Block cobbleBlock, String rock)
	{
		super(mossyCobbleBlock, "mossy_cobblestone", rock);
		this.cobbleBlock = cobbleBlock;
	}

	@Override
	protected void generateRecipes(Map<ResourceLocation, Recipe<?>> recipes)
	{
		recipes.put(this.reformatBlockID("%s_from_vine"), RecipeHelpers.shapeless(this.block, 1, CraftingBookCategory.BUILDING, List.of(
			Ingredient.of(this.cobbleBlock),
			Ingredient.of(Items.VINE))));
		recipes.put(this.reformatBlockID("%s_from_moss_block"), RecipeHelpers.shapeless(this.block, 1, CraftingBookCategory.BUILDING, List.of(
			Ingredient.of(this.cobbleBlock),
			Ingredient.of(Items.MOSS_BLOCK))));
	}

	@Override
	protected void generateTags(TagProvider<Block> blocks, TagProvider<Item> items)
	{
		super.generateTags(blocks, items);
		blocks.tag(Tags.Blocks.COBBLESTONES_MOSSY).add(this.key());
		items.tag(Tags.Items.COBBLESTONES_MOSSY).add(this.itemKey());
	}

}
