package commoble.extrarocks.datagen;

import java.util.List;
import java.util.Map;

import com.mojang.math.Quadrant;

import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;

public class StairsDataProvider extends BlockDataProvider
{
	protected final Block baseBlock;
	protected final ResourceLocation stairsModel;
	protected final ResourceLocation innerModel;
	protected final ResourceLocation outerModel;
	protected final ResourceLocation textureLocation;
	
	public StairsDataProvider(Block stairsBlock, Block baseBlock, ResourceLocation textureLocation)
	{
		super(stairsBlock);
		this.baseBlock = baseBlock;
		this.stairsModel = this.reformatBlockID("block/%s");
		this.innerModel = this.reformatBlockID("block/%s_inner");
		this.outerModel = this.reformatBlockID("block/%s_outer");
		this.textureLocation = textureLocation;
	}

	@Override
	protected void generateBlockStates(Map<ResourceLocation, BlockModelDefinition> blockstates)
	{
		// 40 variants per stair block
		// four facings -- north, south, west, east
		// two halves -- top, bottom
		// five shapes -- straight, inner_right, inner_left, outer_right, outer_left
		blockstates.put(this.blockId(), BlockStateBuilder.variants(variants -> {
			for (Direction facing : StairBlock.FACING.getPossibleValues())
			{
				for (Half half : StairBlock.HALF.getPossibleValues())
				{
					for (StairsShape shape : StairBlock.SHAPE.getPossibleValues())
					{
						// the model for this part depends on the StairsShape
						ResourceLocation partModel =
							shape == StairsShape.INNER_LEFT || shape == StairsShape.INNER_RIGHT ? this.innerModel
								: shape == StairsShape.OUTER_LEFT || shape == StairsShape.OUTER_RIGHT ? this.outerModel
								: this.stairsModel;
						// the x rotation depends on the Half
						Quadrant x = half==Half.TOP ? Quadrant.R180 : Quadrant.R0;
						// the y rotation depends on all three properties
						// the base y rotation depends on the facing, where west = 0 and continuing clockwise
						// (facing.toYRot returns 0 for *south* and 270 for west, so we need to add 90 degrees
						int yRots = facing.get2DDataValue() + 1
							+ (shape == StairsShape.INNER_LEFT || shape == StairsShape.OUTER_LEFT ? 3 : 0)
							+ (half == Half.TOP && shape != StairsShape.STRAIGHT ? 90 : 0);
						Quadrant y = Quadrant.values()[yRots % 4];
						boolean uvlock = x != Quadrant.R0 || y != Quadrant.R0;
						variants.addMultiPropertyVariant(props -> props
							.addPropertyValue(StairBlock.FACING, facing)
							.addPropertyValue(StairBlock.HALF, half)
							.addPropertyValue(StairBlock.SHAPE, shape),
							BlockStateBuilder.model(partModel, x, y, uvlock));
					}
				}
			}
		}));
	}

	@Override
	protected void generateBlockModels(Map<ResourceLocation, SimpleModel> models)
	{
		models.put(this.stairsModel, SimpleModel.create("block/stairs")
			.addTexture("bottom", textureLocation)
			.addTexture("top", textureLocation)
			.addTexture("side", textureLocation));
		models.put(this.innerModel, SimpleModel.create("block/inner_stairs")
			.addTexture("bottom", textureLocation)
			.addTexture("top", textureLocation)
			.addTexture("side", textureLocation));
		models.put(this.outerModel, SimpleModel.create("block/outer_stairs")
			.addTexture("bottom", textureLocation)
			.addTexture("top", textureLocation)
			.addTexture("side", textureLocation));
	}

	@Override
	protected void generateRecipes(Map<ResourceLocation, Recipe<?>> recipes)
	{
		recipes.put(this.blockId(), RecipeHelpers.shaped(this.block, 4, CraftingBookCategory.BUILDING,
			List.of(
				"#  ",
				"## ",
				"###"),
			Map.of('#', Ingredient.of(this.baseBlock))));
		recipes.put(
			IDUtil.reformatID(this.blockId(), "%s_from_%s_stonecutting",
				this.blockId().getPath(),
				IDUtil.blockId(this.baseBlock).getPath()),
			RecipeHelpers.stonecutting(this.block, 1, Ingredient.of(this.baseBlock)));
	}

	@Override
	protected void generateTags(TagProvider<Block> blocks, TagProvider<Item> items)
	{
		super.generateTags(blocks, items);
		blocks.tag(BlockTags.STAIRS).add(this.key());
		items.tag(ItemTags.STAIRS).add(this.itemKey());
	}

}
