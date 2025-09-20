package commoble.extrarocks.datagen;

import java.util.List;
import java.util.Map;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

public class SlabDataProvider extends BlockDataProvider
{
	/** The ID of the cubic block the slab is based on **/
	private final Block baseBlock;
	private final ResourceLocation doubleSlabModel;
	private final ResourceLocation slabModel;
	private final ResourceLocation slabTopModel;
	private final ResourceLocation textureLocation;
	private final ResourceLocation sideTextureLocation;
	private final boolean uniqueDoubleSlabModel;

	public SlabDataProvider(Block slabBlock, Block baseBlock, ResourceLocation textureLocation, ResourceLocation sideTextureLocation, boolean uniqueDoubleSlabModel)
	{
		super(slabBlock);
		this.baseBlock = baseBlock;
		this.doubleSlabModel = uniqueDoubleSlabModel ? this.reformatBlockID("block/%s_double") : IDUtil.reformatID(IDUtil.blockId(this.baseBlock), "block/%s");
		this.slabModel = this.reformatBlockID("block/%s");
		this.slabTopModel = this.reformatBlockID("block/%s_top");
		this.textureLocation = textureLocation;
		this.sideTextureLocation = sideTextureLocation;
		this.uniqueDoubleSlabModel = uniqueDoubleSlabModel;
	}

	@Override
	protected void generateBlockStates(Map<ResourceLocation, BlockModelDefinition> blockstates)
	{
		blockstates.put(this.blockId(), BlockStateBuilder.variants(builder -> builder
			.addVariant(SlabBlock.TYPE, SlabType.BOTTOM, BlockStateBuilder.model(this.slabModel))
			.addVariant(SlabBlock.TYPE, SlabType.DOUBLE, BlockStateBuilder.model(this.doubleSlabModel))
			.addVariant(SlabBlock.TYPE, SlabType.TOP, BlockStateBuilder.model(this.slabTopModel))));
	}

	@Override
	protected void generateBlockModels(Map<ResourceLocation, SimpleModel> models)
	{
		models.put(this.slabModel, SimpleModel.create("block/slab")
			.addTexture("bottom", textureLocation)
			.addTexture("top", textureLocation)
			.addTexture("side", sideTextureLocation));
		models.put(this.slabTopModel, SimpleModel.create("block/slab_top")
			.addTexture("bottom", textureLocation)
			.addTexture("top", textureLocation)
			.addTexture("side", sideTextureLocation));
		if (this.uniqueDoubleSlabModel)
		{
			models.put(this.doubleSlabModel, SimpleModel.create("block/cube_column")
				.addTexture("end", textureLocation)
				.addTexture("side", sideTextureLocation));
		}
	}

	@Override
	protected void generateLootTables(DataGenContext context)
	{
		ResourceLocation lootLocation = this.reformatBlockID("blocks/%s");
		context.lootTables().put(lootLocation, LootTable.lootTable()
			.setParamSet(LootContextParamSets.BLOCK)
			.setRandomSequence(lootLocation)
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(this.block)
					.apply(SetItemCountFunction.setCount(ConstantValue.exactly(2))
						.when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(this.block)
							.setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SlabBlock.TYPE, SlabType.DOUBLE))))
					.apply(ApplyExplosionDecay.explosionDecay())))
			.build());
	}

	@Override
	protected void generateRecipes(Map<ResourceLocation, Recipe<?>> recipes)
	{
		recipes.put(this.blockId(), RecipeHelpers.shaped(this.block, 1, CraftingBookCategory.BUILDING,
			List.of("###"),
			Map.of('#', Ingredient.of(this.baseBlock))));
		recipes.put(
			IDUtil.reformatID(this.blockId(), "%s_from_%s_stonecutting", this.blockId().getPath(), IDUtil.blockId(this.baseBlock).getPath()),
			RecipeHelpers.stonecutting(this.block, 2, Ingredient.of(this.baseBlock)));
	}

	@Override
	protected void generateTags(TagProvider<Block> blocks, TagProvider<Item> items)
	{
		super.generateTags(blocks, items);
		blocks.tag(BlockTags.SLABS).add(this.key());
		items.tag(ItemTags.SLABS).add(this.itemKey());
	}

}
