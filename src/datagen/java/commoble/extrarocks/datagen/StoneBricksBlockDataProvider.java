package commoble.extrarocks.datagen;

import java.util.List;
import java.util.Map;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;


public class StoneBricksBlockDataProvider extends PaletteSwappedBlockDataProvider
{
	protected Block stoneBlock;
	
	public StoneBricksBlockDataProvider(Block block, Block stoneBlock, String rock)
	{
		super(block, "stone_bricks", rock);
		this.stoneBlock = stoneBlock;
	}

	@Override
	protected void generateRecipes(Map<ResourceLocation, Recipe<?>> recipes)
	{
		recipes.put(this.blockId(), RecipeHelpers.shaped(this.block, 1, CraftingBookCategory.BUILDING,
			List.of("##", "##"),
			Map.of('#', Ingredient.of(this.stoneBlock))));
		
		recipes.put(
			IDUtil.reformatID(this.blockId(), "%s_from_%s_stonecutting", this.blockId().getPath(), IDUtil.blockPath(this.stoneBlock)),
			RecipeHelpers.stonecutting(this.block, 1, Ingredient.of(this.stoneBlock)));				
		
		// stone brick slabs, stairs, walls can all be crafted from regular stone
		// easiest way to add them is to addd them from the stone bricks block here:
		ResourceLocation stoneBrick = this.blockId().withPath(stoneBricks -> stoneBricks.substring(0,stoneBricks.length()-1));
		ResourceLocation slabBlockId = stoneBrick.withPath(s -> s + "_slab");
		ResourceLocation stairBlockId = stoneBrick.withPath(s -> s + "_stairs");
		ResourceLocation wallBlockId = stoneBrick.withPath(s -> s + "_wall");
		ResourceLocation stoneBlockId = IDUtil.blockId(stoneBlock);
		recipes.put( 
			IDUtil.reformatID(this.blockId(), "%s_from_%s_stonecutting",
				slabBlockId.getPath(),
				stoneBlockId.getPath()),
			RecipeHelpers.stonecutting(BuiltInRegistries.ITEM.getValue(slabBlockId), 2, Ingredient.of(this.stoneBlock)));
		recipes.put( 
			IDUtil.reformatID(this.blockId(), "%s_from_%s_stonecutting",
				stairBlockId.getPath(),
				stoneBlockId.getPath()),
			RecipeHelpers.stonecutting(BuiltInRegistries.ITEM.getValue(stairBlockId), 1, Ingredient.of(this.stoneBlock)));
		recipes.put( 
			IDUtil.reformatID(this.blockId(), "%s_from_%s_stonecutting",
				slabBlockId.getPath(),
				stoneBlockId.getPath()),
			RecipeHelpers.stonecutting(BuiltInRegistries.ITEM.getValue(wallBlockId), 1, Ingredient.of(this.stoneBlock)));
	}

	@Override
	protected void generateTags(TagProvider<Block> blocks, TagProvider<Item> items)
	{
		super.generateTags(blocks, items);
		blocks.tag(BlockTags.STONE_BRICKS).add(this.key());
		items.tag(ItemTags.STONE_BRICKS).add(this.itemKey());
	}

	
}
