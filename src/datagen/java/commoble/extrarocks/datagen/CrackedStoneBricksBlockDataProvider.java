package commoble.extrarocks.datagen;

import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;

public class CrackedStoneBricksBlockDataProvider extends PaletteSwappedBlockDataProvider
{
	protected final Block stoneBricksBlock;
	
	public CrackedStoneBricksBlockDataProvider(Block crackedBricksBlock, Block stoneBricksBlock, String rock)
	{
		super(crackedBricksBlock, "cracked_stone_bricks", rock);
		this.stoneBricksBlock = stoneBricksBlock;
	}

	@Override
	protected void generateRecipes(Map<ResourceLocation, Recipe<?>> recipes)
	{
		recipes.put(this.blockId(), RecipeHelpers.smelting(this.block, Ingredient.of(this.stoneBricksBlock), 0.1F, CookingBookCategory.BLOCKS));
	}	

	@Override
	protected void generateTags(TagProvider<Block> blocks, TagProvider<Item> items)
	{
		super.generateTags(blocks, items);
		blocks.tag(BlockTags.STONE_BRICKS).add(this.key());
		items.tag(ItemTags.STONE_BRICKS).add(this.itemKey());
	}
}
