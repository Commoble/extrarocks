package commoble.extrarocks.datagen;

import java.util.List;
import java.util.Map;

import com.mojang.math.Quadrant;

import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.WallSide;

public class WallDataProvider extends BlockDataProvider
{
	private final Block baseBlock;
	private final ResourceLocation postModel;
	private final ResourceLocation sideModel;
	private final ResourceLocation tallSideModel;
	private final ResourceLocation itemModel;
	private final ResourceLocation textureLocation;

	public WallDataProvider(Block wallBlock, Block baseBlock, ResourceLocation textureLocation)
	{
		super(wallBlock);
		this.baseBlock = baseBlock;
		this.postModel = this.reformatBlockID("block/%s_post");
		this.sideModel = this.reformatBlockID("block/%s_side");
		this.tallSideModel = this.reformatBlockID("block/%s_side_tall");
		this.itemModel = this.reformatBlockID("block/%s_inventory");
		this.textureLocation = textureLocation;
	}

	@Override
	protected void generateBlockStates(Map<ResourceLocation, BlockModelDefinition> blockstates)
	{
		blockstates.put(this.blockId(), BlockStateBuilder.multipart(parts -> {
			// wall multipart has nine cases
			// one is just up=true
			parts.applyWhen(BlockStateBuilder.model(this.postModel), WallBlock.UP, true);
			
			// then there are two cases for each horizontal direction
			for (Direction side : BlockStateProperties.HORIZONTAL_FACING.getPossibleValues())
			{
				// x-rotation is always 0 for these, uvlock is always true
				// yrotation for wall side cases is 0 for north, rotating clockwise
				// the canonical rotations for horizontal directions are 0 for *south*, rotating clockwise
				// so we can add 180 degrees to adjust this
				Quadrant x = Quadrant.R0;
				Quadrant y = Quadrant.values()[(side.get2DDataValue() + 2) % 4];
				parts.applyWhen(BlockStateBuilder.model(this.sideModel, x, y), WallBlock.PROPERTY_BY_DIRECTION.get(side), WallSide.LOW);
				parts.applyWhen(BlockStateBuilder.model(this.tallSideModel, x, y), WallBlock.PROPERTY_BY_DIRECTION.get(side), WallSide.TALL);
			}
		}));
	}

	@Override
	protected void generateBlockModels(Map<ResourceLocation, SimpleModel> models)
	{
		models.put(this.postModel, SimpleModel.create("block/template_wall_post").addTexture("wall", this.textureLocation));
		models.put(this.sideModel, SimpleModel.create("block/template_wall_side").addTexture("wall", this.textureLocation));
		models.put(this.tallSideModel, SimpleModel.create("block/template_wall_side_tall").addTexture("wall", this.textureLocation));
		models.put(this.itemModel, SimpleModel.create("block/wall_inventory").addTexture("wall", this.textureLocation));
	}

	@Override
	protected void generateClientItems(Map<ResourceLocation, ClientItem> items)
	{
		items.put(this.blockId(), SimpleModel.blockModelWrapper(this.itemModel));
	}

	@Override
	protected void generateRecipes(Map<ResourceLocation, Recipe<?>> recipes)
	{
		recipes.put(this.blockId(), RecipeHelpers.shaped(this.block, 6, CraftingBookCategory.BUILDING,
			List.of("###", "###"),
			Map.of('#', Ingredient.of(this.baseBlock))));
		String stonecuttingFormatString = "%s_from_" + IDUtil.reformatID(IDUtil.blockId(baseBlock), "%s_stonecutting").getPath();
		recipes.put(this.reformatBlockID(stonecuttingFormatString), RecipeHelpers.stonecutting(this.block, 1, Ingredient.of(this.baseBlock)));
	}

	@Override
	protected void generateTags(TagProvider<Block> blocks, TagProvider<Item> items)
	{
		// don't super generate tags, we add ourself to walls which is already mineable
		blocks.tag(BlockTags.WALLS).add(this.key());
		items.tag(ItemTags.WALLS).add(this.itemKey());
	}

	
}
