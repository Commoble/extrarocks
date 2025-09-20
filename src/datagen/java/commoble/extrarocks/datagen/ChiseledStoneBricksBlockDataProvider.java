package commoble.extrarocks.datagen;

import java.util.List;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;

public class ChiseledStoneBricksBlockDataProvider extends PaletteSwappedBlockDataProvider
{
	protected final Block stoneBlock;
	protected final Block bricksBlock;
	protected final Block brickSlabBlock;
	
	public ChiseledStoneBricksBlockDataProvider(Block chiseledBlock, Block stoneBlock, Block bricksBlock, Block brickSlabBlock, String rock)
	{
		super(chiseledBlock, "chiseled_stone_bricks", rock);
		this.stoneBlock = stoneBlock;
		this.bricksBlock = bricksBlock;
		this.brickSlabBlock = brickSlabBlock;
	}

	@Override
	protected void generateRecipes(Map<ResourceLocation, Recipe<?>> recipes)
	{
		recipes.put(this.blockId(), RecipeHelpers.shaped(this.block, 1, CraftingBookCategory.BUILDING, List.of(
				"#",
				"#"),
			Map.of('#', Ingredient.of(this.brickSlabBlock))));
		
		ResourceLocation stoneStonecuttingRecipeID = IDUtil.reformatID(this.blockId(), "%s_from_%s_stonecutting", this.blockId().getPath(), IDUtil.blockId(this.stoneBlock).getPath());
		recipes.put(stoneStonecuttingRecipeID, RecipeHelpers.stonecutting(this.block, 1, Ingredient.of(this.stoneBlock)));
		
		ResourceLocation bricksStonecuttingRecipeID = IDUtil.reformatID(this.blockId(), "%s_from_%s_stonecutting", this.blockId().getPath(), IDUtil.blockId(this.bricksBlock).getPath());
		recipes.put(bricksStonecuttingRecipeID, RecipeHelpers.stonecutting(this.block, 1, Ingredient.of(this.bricksBlock)));
	}

	@Override
	protected void generateTags(TagProvider<Block> blocks, TagProvider<Item> items)
	{
		super.generateTags(blocks, items);
		blocks.tag(BlockTags.STONE_BRICKS).add(this.key());
		items.tag(ItemTags.STONE_BRICKS).add(this.itemKey());
	}

}
