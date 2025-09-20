package commoble.extrarocks.datagen;

import java.util.List;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;

public class MossyStoneBricksBlockDataProvider extends PaletteSwappedBlockDataProvider
{
	protected final Block stoneBrickBlock;
	
	public MossyStoneBricksBlockDataProvider(Block mossyBrickBlock, Block stoneBrickBlock, String rock)
	{
		super(mossyBrickBlock, "mossy_stone_bricks", rock);
		this.stoneBrickBlock = stoneBrickBlock;
	}

	@Override
	protected void generateRecipes(Map<ResourceLocation, Recipe<?>> recipes)
	{
		recipes.put(this.reformatBlockID("%s_from_vine"), RecipeHelpers.shapeless(this.block, 1, CraftingBookCategory.BUILDING, List.of(
			Ingredient.of(this.stoneBrickBlock),
			Ingredient.of(Items.VINE))));
		recipes.put(this.reformatBlockID("%s_from_moss_block"), RecipeHelpers.shapeless(this.block, 1, CraftingBookCategory.BUILDING, List.of(
			Ingredient.of(this.stoneBrickBlock),
			Ingredient.of(Items.MOSS_BLOCK))));
	}

	@Override
	protected void generateTags(TagProvider<Block> blocks, TagProvider<Item> items)
	{
		super.generateTags(blocks, items);
		blocks.tag(BlockTags.STONE_BRICKS).add(this.key());
		items.tag(ItemTags.STONE_BRICKS).add(this.itemKey());
	}

}
