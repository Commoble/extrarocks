package commoble.extrarocks.datagen;

import java.util.List;
import java.util.Map;

import com.mojang.math.Quadrant;

import net.minecraft.advancements.critereon.DataComponentMatchers;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.neoforged.neoforge.common.Tags;

public class StoneBlockDataProvider extends BlockDataProvider
{	
	private final Item cobblestoneItem;
	
	public StoneBlockDataProvider(Block stoneBlock, Item cobblestoneItem)
	{
		super(stoneBlock);
		this.cobblestoneItem = cobblestoneItem;
	}	

	@Override
	protected void generateBlockStates(Map<ResourceLocation, BlockModelDefinition> blockstates)
	{
		ResourceLocation blockModel = this.reformatBlockID("block/%s");
		ResourceLocation mirroredBlockModel = this.reformatBlockID("block/%s_mirrored");
		blockstates.put(this.blockId(), BlockStateBuilder.singleVariant(BlockStateBuilder.randomModels(
			new Weighted<>(BlockStateBuilder.model(blockModel), 1),
			new Weighted<>(BlockStateBuilder.model(mirroredBlockModel), 1),
			new Weighted<>(BlockStateBuilder.model(blockModel, Quadrant.R0, Quadrant.R180), 1),
			new Weighted<>(BlockStateBuilder.model(mirroredBlockModel, Quadrant.R0, Quadrant.R180), 1))));
	}
	
	@Override
	protected void generateBlockModels(Map<ResourceLocation, SimpleModel> models)
	{
		ResourceLocation blockModel = this.reformatBlockID("block/%s");
		ResourceLocation textureLocation = blockModel;
		ResourceLocation mirroredBlockModel = this.reformatBlockID("block/%s_mirrored");
		models.put(blockModel, SimpleModel.create("block/cube_all")
			.addTexture("all", textureLocation));
		models.put(mirroredBlockModel, SimpleModel.create("block/cube_mirrored_all")
			.addTexture("all", textureLocation));
	}

	@Override
	protected void generateLootTables(DataGenContext context)
	{
		ResourceLocation lootLocation = this.reformatBlockID("blocks/%s");
		context.lootTables().put(lootLocation, LootTable.lootTable()
			.setParamSet(LootContextParamSets.BLOCK)
			.setRandomSequence(lootLocation)
			.withPool(LootPool.lootPool().add(AlternativesEntry.alternatives(
				// with silk touch, drop the block
				LootItem.lootTableItem(this.block)
					.when(MatchTool.toolMatches(ItemPredicate.Builder.item()
						.withComponents(DataComponentMatchers.Builder.components()
							.partial(DataComponentPredicates.ENCHANTMENTS, EnchantmentsPredicate.enchantments(List.of(
								new EnchantmentPredicate(FakeHolder.of(Enchantments.SILK_TOUCH), MinMaxBounds.Ints.atLeast(1)))))
						.build()))),
				// otherwise, drop cobblestone
				LootItem.lootTableItem(this.cobblestoneItem)
					.apply(ApplyExplosionDecay.explosionDecay()))))
			.build());
	}

	@Override
	protected void generateRecipes(Map<ResourceLocation, Recipe<?>> recipes)
	{
		recipes.put(this.blockId(), RecipeHelpers.smelting(this.block, Ingredient.of(this.cobblestoneItem), 0.1F, CookingBookCategory.BLOCKS));
	}

	@Override
	protected void generateTags(TagProvider<Block> blocks, TagProvider<Item> items)
	{
		super.generateTags(blocks, items);
		blocks.tag(BlockTags.GOATS_SPAWNABLE_ON).add(this.key());
		blocks.tag(BlockTags.SNAPS_GOAT_HORN).add(this.key());
		blocks.tag(BlockTags.BASE_STONE_OVERWORLD).add(this.key());
		blocks.tag(Tags.Blocks.ORE_BEARING_GROUND_STONE).add(this.key());
		blocks.tag(Tags.Blocks.STONES).add(this.key());
		items.tag(Tags.Items.ORE_BEARING_GROUND_STONE).add(this.itemKey());
		items.tag(ExtraRocksDatagen.EXTRA_STONES).add(this.itemKey());
	}
}
