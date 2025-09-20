package commoble.extrarocks.datagen;

import java.util.List;
import java.util.Map;

import net.minecraft.advancements.critereon.DataComponentMatchers;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

public class InfestedBlockDataProvider extends BlockDataProvider
{
	protected final Block uninfestedBlock;
	protected final ResourceLocation blockModel;
	
	public InfestedBlockDataProvider(Block uninfestedBlock)
	{
		super(BuiltInRegistries.BLOCK.getValue(IDUtil.reformatID(IDUtil.blockId(uninfestedBlock), "infested_%s")));
		this.uninfestedBlock = uninfestedBlock;
		this.blockModel = IDUtil.reformatID(IDUtil.blockId(uninfestedBlock), "block/%s");
	}

	@Override
	protected void generateBlockStates(Map<ResourceLocation, BlockModelDefinition> blockstates)
	{
		blockstates.put(this.blockId(), BlockStateBuilder.singleVariant(BlockStateBuilder.model(this.blockModel)));
	}
	
	@Override
	protected void generateBlockModels(Map<ResourceLocation, SimpleModel> models)
	{
		// noop, the blockstate and item model use the uninfested model
	}

	@Override
	protected void generateClientItems(Map<ResourceLocation, ClientItem> items)
	{
		items.put(this.blockId(), SimpleModel.blockModelWrapper(this.blockModel));
	}

	@Override
	protected void generateLootTables(DataGenContext context)
	{
		ResourceLocation lootLocation = this.reformatBlockID("blocks/%s");
		context.lootTables().put(lootLocation, LootTable.lootTable()
			.setParamSet(LootContextParamSets.BLOCK)
			.setRandomSequence(lootLocation)
			.withPool(LootPool.lootPool()
				.when(MatchTool.toolMatches(ItemPredicate.Builder.item()
					.withComponents(DataComponentMatchers.Builder.components()
						.partial(DataComponentPredicates.ENCHANTMENTS, EnchantmentsPredicate.enchantments(List.of(
							new EnchantmentPredicate(FakeHolder.of(Enchantments.SILK_TOUCH), MinMaxBounds.Ints.atLeast(1)))))
					.build())))
				.add(LootItem.lootTableItem(this.block)))
			.build());
	}
}
