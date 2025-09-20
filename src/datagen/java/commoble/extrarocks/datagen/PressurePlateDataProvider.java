package commoble.extrarocks.datagen;

import java.util.List;
import java.util.Map;

import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;

public class PressurePlateDataProvider extends BlockDataProvider
{
	protected final Block baseBlock;
	protected final ResourceLocation plateModel;
	protected final ResourceLocation plateDownModel;
	protected final ResourceLocation textureLocation;

	public PressurePlateDataProvider(Block plateBlock, Block baseBlock, ResourceLocation textureLocation)
	{
		super(plateBlock);
		this.baseBlock = baseBlock;
		this.plateModel = this.reformatBlockID("block/%s");
		this.plateDownModel = this.reformatBlockID("block/%s_down");
		this.textureLocation = textureLocation;
	}

	@Override
	protected void generateBlockStates(Map<ResourceLocation, BlockModelDefinition> blockstates)
	{
		blockstates.put(this.blockId(), BlockStateBuilder.variants(variants -> variants
			.addVariant(PressurePlateBlock.POWERED, false, BlockStateBuilder.model(this.plateModel))
			.addVariant(PressurePlateBlock.POWERED, true, BlockStateBuilder.model(this.plateDownModel))));
	}

	@Override
	protected void generateBlockModels(Map<ResourceLocation, SimpleModel> models)
	{
		models.put(this.plateModel, SimpleModel.create("block/pressure_plate_up").addTexture("texture", this.textureLocation));
		models.put(this.plateDownModel, SimpleModel.create("block/pressure_plate_down").addTexture("texture", this.textureLocation));
	}

	@Override
	protected void generateRecipes(Map<ResourceLocation, Recipe<?>> recipes)
	{
		recipes.put(this.blockId(), RecipeHelpers.shaped(this.block, 1, CraftingBookCategory.REDSTONE,
			List.of("##"),
			Map.of('#', Ingredient.of(this.baseBlock))));
	}

	@Override
	protected void generateTags(TagProvider<Block> blocks, TagProvider<Item> items)
	{
		super.generateTags(blocks, items);
		blocks.tag(BlockTags.STONE_PRESSURE_PLATES).add(this.key());
	}

	
}
