package commoble.extrarocks.datagen;

import java.util.List;

import net.minecraft.advancements.critereon.DataComponentMatchers;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.component.predicates.DataComponentPredicates;
import net.minecraft.core.component.predicates.EnchantmentsPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public class SupernuminalOreDataProvider extends OreBlockDataProvider
{
	protected final NumberProvider range;
	protected final LootItemFunction.Builder bonusFunction;
	
	public SupernuminalOreDataProvider(Block oreBlock, String rock, String ore, Item droppedItem, NumberProvider range, LootItemFunction.Builder bonusFunction, List<TagKey<Block>> blockTags, List<TagKey<Item>> itemTags)
	{
		super(oreBlock, rock, ore, droppedItem, blockTags, itemTags);
		this.range = range;
		this.bonusFunction = bonusFunction;
	}

	@Override
	protected void generateLootTables(DataGenContext context)
	{
		ResourceLocation lootLocation = this.reformatBlockID("blocks/%s");
		Item blockItem = BuiltInRegistries.ITEM.getValue(this.blockId());
		context.lootTables().put(lootLocation, LootTable.lootTable()
			.setParamSet(LootContextParamSets.BLOCK)
			.setRandomSequence(lootLocation)
			.withPool(LootPool.lootPool().add(AlternativesEntry.alternatives(
				// with silk touch, drop the block
				LootItem.lootTableItem(blockItem)
					.when(MatchTool.toolMatches(ItemPredicate.Builder.item()
						.withComponents(DataComponentMatchers.Builder.components()
							.partial(DataComponentPredicates.ENCHANTMENTS, EnchantmentsPredicate.enchantments(List.of(
								new EnchantmentPredicate(FakeHolder.of(Enchantments.SILK_TOUCH), MinMaxBounds.Ints.atLeast(1)))))
						.build()))),
				// otherwise, drop the raw ore with fortune bonus and explosion decay
				LootItem.lootTableItem(this.droppedItem)
	                .apply(SetItemCountFunction.setCount(this.range))
	                .apply(this.bonusFunction))))
			.build());
	}

	
}
