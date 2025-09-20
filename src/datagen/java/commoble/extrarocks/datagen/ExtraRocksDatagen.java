package commoble.extrarocks.datagen;

import java.util.List;
import java.util.Map;

import net.commoble.extrarocks.ExtraRocks;
import net.commoble.extrarocks.RockType;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSources;
import net.minecraft.client.renderer.texture.atlas.sources.PalettedPermutations;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput.Target;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.data.event.GatherDataEvent;

// we keep the datagen in a separate class so it doesn't get built into the production jar
@Mod(ExtraRocks.MODID)
@EventBusSubscriber(modid=ExtraRocks.MODID)
public class ExtraRocksDatagen
{	
	private static TagKey<Item> itemTag(String path)
	{
		return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(ExtraRocks.MODID, path));
	}
	public static final TagKey<Item> EXTRA_STONES = itemTag("stones");
	public static final TagKey<Item> EXTRA_COBBLESTONES = itemTag("cobblestones");
	public static final TagKey<Item> EXTRA_COAL_ORES = itemTag("coal_ores");
	public static final TagKey<Item> EXTRA_IRON_ORES = itemTag("iron_ores");
	public static final TagKey<Item> EXTRA_REDSTONE_ORES = itemTag("redstone_ores");
	public static final TagKey<Item> EXTRA_GOLD_ORES = itemTag("gold_ores");
	public static final TagKey<Item> EXTRA_DIAMOND_ORES = itemTag("diamond_ores");
	public static final TagKey<Item> EXTRA_LAPIS_ORES = itemTag("lapis_ores");
	public static final TagKey<Item> EXTRA_EMERALD_ORES = itemTag("emerald_ores");
	public static final TagKey<Item> EXTRA_COPPER_ORES = itemTag("copper_ores");
	
	@SubscribeEvent
	public static void onGatherData(GatherDataEvent.Client event)
	{
		DataGenerator generator = event.getGenerator();
		DataGenContext context = DataGenContext.of(event);
		
		List<String> oreTypes = List.of("coal", "iron", "redstone", "gold", "diamond", "lapis", "emerald", "copper");
		List<String> blockTextures = List.of("cobblestone", "mossy_cobblestone", "stone_bricks", "cracked_stone_bricks", "mossy_stone_bricks", "smooth_stone_slab_side", "smooth_stone", "chiseled_stone_bricks");
		
		for (RockType rock : RockType.values())
		{
			String rockName = rock.getSerializedName();
			ResourceLocation baseRockID = ResourceLocation.fromNamespaceAndPath(ExtraRocks.MODID, rockName);
			
			Block stoneBlock = BuiltInRegistries.BLOCK.getValue(baseRockID);
			ResourceLocation cobbleBlockID = IDUtil.reformatID(baseRockID, "cobbled_%s");
			Block cobblestoneBlock = BuiltInRegistries.BLOCK.getValue(cobbleBlockID);
			new StoneBlockDataProvider(stoneBlock, cobblestoneBlock.asItem())
				.run(context);
			new InfestedStoneDataProvider(stoneBlock)
				.run(context);
			new CraftableVariantsDataProvider(stoneBlock, baseRockID, baseRockID.withPath(s -> "block/" + s))
				.run(context);
			
			new CobblestoneBlockDataProvider(cobblestoneBlock, rockName)
				.run(context);
			new CraftableVariantsDataProvider(cobblestoneBlock, IDUtil.blockId(cobblestoneBlock), IDUtil.makePermutationTextureLocation("cobblestone", rockName))
				.run(context);
			new InfestedCobblestoneBlockDataProvider(cobblestoneBlock)
				.run(context);
			
			ResourceLocation mossyCobbleBlockID = IDUtil.reformatID(baseRockID, "mossy_cobbled_%s");
			Block mossyCobbleBlock = BuiltInRegistries.BLOCK.getValue(mossyCobbleBlockID);
			new MossyCobblestoneBlockDataProvider(mossyCobbleBlock, cobblestoneBlock, rockName)
				.run(context);
			// no infested mossy cobblestone
			new CraftableVariantsDataProvider(mossyCobbleBlock, mossyCobbleBlockID, IDUtil.makePermutationTextureLocation("mossy_cobblestone", rockName))
				.run(context);
			
			ResourceLocation bricksBlockID = IDUtil.reformatID(baseRockID, "%s_bricks");
			ResourceLocation depluralizedBrick = IDUtil.reformatID(baseRockID, "%s_brick");
			Block bricksBlock = BuiltInRegistries.BLOCK.getValue(bricksBlockID);
			new StoneBricksBlockDataProvider(bricksBlock, stoneBlock, rockName)
				.run(context);
			new InfestedBlockDataProvider(bricksBlock)
				.run(context);
			new CraftableVariantsDataProvider(bricksBlock, depluralizedBrick, IDUtil.makePermutationTextureLocation("stone_bricks", rockName))
				.run(context);
			
			ResourceLocation mossyBricksBlockID = IDUtil.reformatID(baseRockID, "mossy_%s_bricks");
			ResourceLocation depluralizedMossyBrick = IDUtil.reformatID(baseRockID, "mossy_%s_brick");
			Block mossyBrickBlock = BuiltInRegistries.BLOCK.getValue(mossyBricksBlockID);
			new MossyStoneBricksBlockDataProvider(mossyBrickBlock, bricksBlock, rockName)
				.run(context);
			new InfestedBlockDataProvider(mossyBrickBlock)
				.run(context);
			new CraftableVariantsDataProvider(mossyBrickBlock, depluralizedMossyBrick, IDUtil.makePermutationTextureLocation("mossy_stone_bricks", rockName))
				.run(context);
			
			// cracked bricks have no variants except for infested block
			ResourceLocation crackedBricksBlockID = IDUtil.reformatID(baseRockID, "cracked_%s_bricks");
			Block crackedBricksBlock = BuiltInRegistries.BLOCK.getValue(crackedBricksBlockID);
			new CrackedStoneBricksBlockDataProvider(crackedBricksBlock, bricksBlock, rockName)
				.run(context);
			new InfestedBlockDataProvider(crackedBricksBlock)
				.run(context);
			
			// chiseled stone bricks -- no variants except for infested block
			ResourceLocation chiseledBricksBlockID = IDUtil.reformatID(baseRockID, "chiseled_%s_bricks");
			Block chiseledBricksBlock = BuiltInRegistries.BLOCK.getValue(chiseledBricksBlockID);
			ResourceLocation brickSlabBlockID = IDUtil.reformatID(baseRockID, "%s_brick_slab");
			Block brickSlabBlock = BuiltInRegistries.BLOCK.getValue(brickSlabBlockID);
			new ChiseledStoneBricksBlockDataProvider(chiseledBricksBlock, stoneBlock, bricksBlock, brickSlabBlock, rockName)
				.run(context);
			new InfestedBlockDataProvider(chiseledBricksBlock)
				.run(context);
			
			// smooth stone block -- no variants
			ResourceLocation smoothStoneBlockID = IDUtil.reformatID(baseRockID, "smooth_%s");
			Block smoothStoneBlock = BuiltInRegistries.BLOCK.getValue(smoothStoneBlockID);
			new SmoothStoneBlockDataProvider(smoothStoneBlock, stoneBlock, rockName).run(context);
			
			// smooth stone slab -- no variants
			ResourceLocation smoothStoneSlabBlockID = IDUtil.reformatID(smoothStoneBlockID, "%s_slab");
			ResourceLocation smoothStoneTextureLocation = IDUtil.makePermutationTextureLocation("smooth_stone", rockName);
			ResourceLocation smoothStoneSlabSideTextureLocation = IDUtil.makePermutationTextureLocation("smooth_stone_slab_side", rockName);
			Block smoothStoneSlabBlock = BuiltInRegistries.BLOCK.getValue(smoothStoneSlabBlockID);
			new SlabDataProvider(smoothStoneSlabBlock, smoothStoneBlock, smoothStoneTextureLocation, smoothStoneSlabSideTextureLocation, true).run(context);
			
			
			
			// ores
			new OreBlockDataProvider(BuiltInRegistries.BLOCK.getValue(IDUtil.reformatID(baseRockID, "%s_gold_ore")), rockName, "gold", Items.RAW_GOLD,
				List.of(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Blocks.ORES_GOLD),
				List.of(Tags.Items.ORE_RATES_SINGULAR, EXTRA_GOLD_ORES))
				.run(context);
			new OreBlockDataProvider(BuiltInRegistries.BLOCK.getValue(IDUtil.reformatID(baseRockID, "%s_iron_ore")), rockName, "iron", Items.RAW_IRON,
				List.of(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Blocks.ORES_IRON),
				List.of(Tags.Items.ORE_RATES_SINGULAR, EXTRA_IRON_ORES))
				.run(context);
			new OreBlockDataProvider(BuiltInRegistries.BLOCK.getValue(IDUtil.reformatID(baseRockID, "%s_coal_ore")), rockName, "coal", Items.COAL,
				List.of(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Blocks.ORES_COAL),
				List.of(Tags.Items.ORE_RATES_SINGULAR, EXTRA_COAL_ORES))
				.run(context);
			new SupernuminalOreDataProvider(BuiltInRegistries.BLOCK.getValue(IDUtil.reformatID(baseRockID, "%s_lapis_ore")), rockName, "lapis", Items.LAPIS_LAZULI, UniformGenerator.between(4F,9F), ApplyBonusCount.addOreBonusCount(FakeHolder.of(Enchantments.FORTUNE)),
				List.of(Tags.Blocks.ORE_RATES_DENSE, Tags.Blocks.ORES_LAPIS),
				List.of(Tags.Items.ORE_RATES_DENSE, EXTRA_LAPIS_ORES))
				.run(context);
			new OreBlockDataProvider(BuiltInRegistries.BLOCK.getValue(IDUtil.reformatID(baseRockID, "%s_diamond_ore")), rockName, "diamond", Items.DIAMOND,
				List.of(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Blocks.ORES_DIAMOND),
				List.of(Tags.Items.ORE_RATES_SINGULAR, EXTRA_DIAMOND_ORES))
				.run(context);
			new SupernuminalOreDataProvider(BuiltInRegistries.BLOCK.getValue(IDUtil.reformatID(baseRockID, "%s_redstone_ore")), rockName, "redstone", Items.REDSTONE, UniformGenerator.between(4F,5F), ApplyBonusCount.addUniformBonusCount(FakeHolder.of(Enchantments.FORTUNE)),
				List.of(Tags.Blocks.ORE_RATES_DENSE, Tags.Blocks.ORES_REDSTONE),
				List.of(Tags.Items.ORE_RATES_DENSE, EXTRA_REDSTONE_ORES))
				.run(context);
			new OreBlockDataProvider(BuiltInRegistries.BLOCK.getValue(IDUtil.reformatID(baseRockID, "%s_emerald_ore")), rockName, "emerald", Items.EMERALD,
				List.of(Tags.Blocks.ORE_RATES_SINGULAR, Tags.Blocks.ORES_EMERALD),
				List.of(Tags.Items.ORE_RATES_SINGULAR, EXTRA_EMERALD_ORES))
				.run(context);
			new OreBlockDataProvider(BuiltInRegistries.BLOCK.getValue(IDUtil.reformatID(baseRockID, "%s_copper_ore")), rockName, "copper", Items.RAW_COPPER,
				List.of(Tags.Blocks.ORE_RATES_DENSE, Tags.Blocks.ORES_COPPER),
				List.of(Tags.Items.ORE_RATES_DENSE, EXTRA_COPPER_ORES))
				.run(context);
			
			// generate paletted permutations
			for (String blockTexture : blockTextures)
			{
				context.blocksAtlas().add(new PalettedPermutations(
					List.of(ResourceLocation.withDefaultNamespace("block/" + blockTexture)),
					ResourceLocation.fromNamespaceAndPath(ExtraRocks.MODID, "palette_keys/" + blockTexture),
					Map.of(ExtraRocks.MODID + "/" + rockName, ResourceLocation.fromNamespaceAndPath(ExtraRocks.MODID, "permutations/" + blockTexture + "/" + rockName)),
					"/"
				));
			}
		}

		for (String ore : oreTypes)
		{
			context.blocksAtlas().add(new PalettedPermutations(
				List.of(ResourceLocation.withDefaultNamespace("block/" + ore + "_ore")),
				ResourceLocation.fromNamespaceAndPath(ExtraRocks.MODID, "palette_keys/" + ore),
				Map.of(ExtraRocks.MODID + "/ore_overlay", ResourceLocation.fromNamespaceAndPath(ExtraRocks.MODID, "permutations/ore_overlays/" + ore)),
				"/"
			));
			
			String itemName = switch(ore) {
				case "lapis" -> "lapis_lazuli";
				case "iron" -> "iron_ingot";
				case "gold" -> "gold_ingot";
				case "copper" -> "copper_ingot";
				default -> ore;
			};
			float xp = switch(ore) {
				case "redstone", "iron", "copper" -> 0.7F;
				case "coal" -> 0.1F;
				case "emerald", "diamond", "gold" -> 1F;
				case "lapis" -> 0.2F;
				default -> 0.1F;
			};
			Item item = BuiltInRegistries.ITEM.getValue(ResourceLocation.withDefaultNamespace(itemName));
			TagKey<Item> oreTag = itemTag(ore + "_ores");
			context.recipes().put(ResourceLocation.fromNamespaceAndPath(ExtraRocks.MODID, String.format("%s_from_smelting_extrarocks_%s_ore", itemName, ore)),
				RecipeHelpers.smelting(item, RecipeHelpers.tagIngredient(oreTag), xp, CookingBookCategory.MISC));
			context.recipes().put(ResourceLocation.fromNamespaceAndPath(ExtraRocks.MODID, String.format("%s_from_blasting_extrarocks_%s_ore", itemName, ore)),
				RecipeHelpers.blasting(item, RecipeHelpers.tagIngredient(oreTag), xp, CookingBookCategory.MISC));
		}
		
		Map<ResourceLocation, List<SpriteSource>> atlases = Map.of(ResourceLocation.withDefaultNamespace("blocks"), context.blocksAtlas());		
		
		// misc things
		context.lang().add("itemGroup." + ExtraRocks.MODID, "Extra Rocks");

		context.itemTags().tag(Tags.Items.COBBLESTONES_NORMAL).addTag(ExtraRocksDatagen.EXTRA_COBBLESTONES);
		context.itemTags().tag(Tags.Items.STONES).addTag(ExtraRocksDatagen.EXTRA_STONES);
		context.itemTags().tag(Tags.Items.ORES_COAL).addTag(EXTRA_COAL_ORES);
		context.itemTags().tag(Tags.Items.ORES_COPPER).addTag(EXTRA_COPPER_ORES);
		context.itemTags().tag(Tags.Items.ORES_DIAMOND).addTag(EXTRA_DIAMOND_ORES);
		context.itemTags().tag(Tags.Items.ORES_EMERALD).addTag(EXTRA_EMERALD_ORES);
		context.itemTags().tag(Tags.Items.ORES_GOLD).addTag(EXTRA_GOLD_ORES);
		context.itemTags().tag(Tags.Items.ORES_IRON).addTag(EXTRA_IRON_ORES);
		context.itemTags().tag(Tags.Items.ORES_LAPIS).addTag(EXTRA_LAPIS_ORES);
		context.itemTags().tag(Tags.Items.ORES_REDSTONE).addTag(EXTRA_REDSTONE_ORES);
		
		// add a recipe to stonecut stones or cobblestones into vanilla cobblestones
		context.recipes().put(ResourceLocation.fromNamespaceAndPath(ExtraRocks.MODID, "cobblestone_from_extra_stone_stonecutting"),
			RecipeHelpers.stonecutting(Blocks.COBBLESTONE, 1, RecipeHelpers.tagIngredient(EXTRA_STONES)));
		context.recipes().put(ResourceLocation.fromNamespaceAndPath(ExtraRocks.MODID, "cobblestone_from_extra_cobblestone_stonecutting"),
			RecipeHelpers.stonecutting(Blocks.COBBLESTONE, 1, RecipeHelpers.tagIngredient(EXTRA_COBBLESTONES)));

		var registries = event.getLookupProvider();
		event.addProvider(JsonDataProvider.create(registries, generator, Target.RESOURCE_PACK, "blockstates", BlockModelDefinition.CODEC, context.blockStates()));
		event.addProvider(JsonDataProvider.create(registries, generator, Target.RESOURCE_PACK, "models", SimpleModel.CODEC, context.models()));
		event.addProvider(JsonDataProvider.create(registries, generator, Target.RESOURCE_PACK, "items", ClientItem.CODEC, context.items()));
		event.addProvider(JsonDataProvider.create(registries, generator, Target.DATA_PACK, "loot_table", LootTable.DIRECT_CODEC, context.lootTables()));
		event.addProvider(JsonDataProvider.create(registries, generator, Target.DATA_PACK, "recipe", Recipe.CODEC, context.recipes()));
		event.addProvider(context.blockTags());
		event.addProvider(context.itemTags());
		event.addProvider(JsonDataProvider.create(registries, generator, Target.RESOURCE_PACK, "atlases", SpriteSources.FILE_CODEC, atlases));
		event.addProvider(context.lang());
	}
}
