package commoble.extrarocks.datagen;

import java.util.List;
import java.util.Map;

import net.commoble.extrarocks.ExtraRocks;
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
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;

public class OreBlockDataProvider extends BlockDataProvider
{
	protected final ResourceLocation rockTexture;
	protected final String rock;
	protected final String ore;
	protected final Item droppedItem;
	protected final List<TagKey<Block>> blockTags;
	protected final List<TagKey<Item>> itemTags;
	
	public OreBlockDataProvider(Block oreBlock, String rock, String ore, Item droppedItem, List<TagKey<Block>> blockTags, List<TagKey<Item>> itemTags)
	{
		super(oreBlock);
		this.rockTexture = ResourceLocation.fromNamespaceAndPath(ExtraRocks.MODID, String.format("block/%s", rock));
		this.rock = rock;
		this.ore = ore;
		this.droppedItem = droppedItem;
		this.blockTags = blockTags;
		this.itemTags = itemTags;
	}

	@Override
	protected void generateBlockModels(Map<ResourceLocation, SimpleModel> models)
	{
		ResourceLocation blockModel = this.reformatBlockID("block/%s");
		models.put(blockModel, SimpleModel.create(ResourceLocation.fromNamespaceAndPath(ExtraRocks.MODID, "block/ore_template"))
			.addTexture("rock", this.rockTexture)
			// paletted permutations use the namespace of the original texture
			.addTexture("ore", ResourceLocation.withDefaultNamespace(String.format("block/%s_ore/%s/ore_overlay", this.ore, ExtraRocks.MODID))));
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
					.apply(ApplyBonusCount.addOreBonusCount(FakeHolder.of(Enchantments.FORTUNE)))
					.apply(ApplyExplosionDecay.explosionDecay()))))
			.build());
	}

	@Override
	protected void generateTags(TagProvider<Block> blocks, TagProvider<Item> items)
	{
		super.generateTags(blocks, items);
		for (var tag : this.blockTags)
		{
			blocks.tag(tag).add(this.key());
		}
		for (var tag : this.itemTags)
		{
			items.tag(tag).add(this.itemKey());
		}
	}
}
