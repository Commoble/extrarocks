package net.commoble.extrarocks;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.commoble.extrarocks.client.ClientProxy;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.InfestedBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RedStoneOreBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(ExtraRocks.MODID)
public class ExtraRocks
{
	public static final String MODID = "extrarocks";
	
	public static ExtraRocks INSTANCE;
	
	public ExtraRocks(IEventBus modBus)
	{
		INSTANCE = this;
		
		final IEventBus forgeBus = NeoForge.EVENT_BUS;
		
		final DeferredRegister.Blocks blocks = defreg(DeferredRegister::createBlocks);
		final DeferredRegister.Items items = defreg(DeferredRegister::createItems);
		final DeferredRegister<CreativeModeTab> tabs = defreg(Registries.CREATIVE_MODE_TAB);
		
		final DeferredHolder<Item,Item> iconItem = DeferredHolder.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(MODID, "dolomite"));
		
		tabs.register(MODID, () -> CreativeModeTab.builder()
			.icon(() -> new ItemStack(iconItem.get()))
			.displayItems(items.getEntries())
			.title(Component.translatable("itemGroup." + ExtraRocks.MODID))
			.build());
		
		final BiFunction<String, Block, BlockItemHolder> blockRegistrator = (name,block)->
			registerBlockWithItem(name,blocks,items, Block::new, BlockBehaviour.Properties.ofFullCopy(block));
			
		final Map<RockType,Map<ResourceLocation,BlockItemHolder>> rockToVanillaToBlockMap = RockType.ROCK_TO_VANILLA_TO_ROCK_BLOCK;
		
		Arrays.stream(RockType.values()).forEach(rock ->
		{
			final Map<ResourceLocation,BlockItemHolder> vanillaToBlockMap = rockToVanillaToBlockMap.get(rock);
			final BiFunction<Block,String,BlockItemHolder> basicLookupRegistrator = (vanillaBlock,rockVariantName)->
			{
				BlockItemHolder pair = blockRegistrator.apply(rockVariantName,vanillaBlock);
				vanillaToBlockMap.put(BuiltInRegistries.BLOCK.getKey(vanillaBlock), pair);
				return pair;
			};
			
			final Consumer<BlockRegistrationData> blockVariantRegistrator = data->
				registerBlockWithVariants(vanillaToBlockMap,blocks,items,data);
			
			final String rockName = rock.getSerializedName();
			// register blocks and items for the six base blocks * the six variants of each
			blockVariantRegistrator.accept(new BlockRegistrationData(rockName, Blocks.STONE)
				.withVanillaStairs(Blocks.STONE_STAIRS)
				.withVanillaSlab(Blocks.STONE_SLAB)
				.withVanillaButton(Blocks.STONE_BUTTON)
				.withVanillaPressurePlate(Blocks.STONE_PRESSURE_PLATE)
				.withVanillaSilverfish(Blocks.INFESTED_STONE));
			
			String cobbleName = "cobbled_"+rockName;
			blockVariantRegistrator.accept(new BlockRegistrationData(cobbleName, Blocks.COBBLESTONE)
				.withVanillaStairs(Blocks.COBBLESTONE_STAIRS)
				.withVanillaSlab(Blocks.COBBLESTONE_SLAB)
				.withVanillaWall(Blocks.COBBLESTONE_WALL)
				.withVanillaSilverfish(Blocks.INFESTED_COBBLESTONE));
			
			String mossyCobbleName = "mossy_" + cobbleName;
			blockVariantRegistrator.accept(new BlockRegistrationData(mossyCobbleName, Blocks.MOSSY_COBBLESTONE)
				.withVanillaStairs(Blocks.MOSSY_COBBLESTONE_STAIRS)
				.withVanillaSlab(Blocks.MOSSY_COBBLESTONE_SLAB)
				.withVanillaWall(Blocks.MOSSY_COBBLESTONE_WALL));
			// mossy cobble doesn't have an invested variant in vanilla
			
			String bricksName = rockName+"_bricks";
			String depluralizedBricks = rockName + "_brick";
			blockVariantRegistrator.accept(new BlockRegistrationData(bricksName, Blocks.STONE_BRICKS)
				.withVanillaStairs(Blocks.STONE_BRICK_STAIRS)
				.withVanillaSlab(Blocks.STONE_BRICK_SLAB)
				.withVanillaWall(Blocks.STONE_BRICK_WALL)
				.withVanillaSilverfish(Blocks.INFESTED_STONE_BRICKS)
				.withDepluralizedName(depluralizedBricks));
			
			String mossyBricksName = "mossy_"+bricksName;
			String depluralizedMossyBricks = "mossy_" + depluralizedBricks;
			blockVariantRegistrator.accept(new BlockRegistrationData(mossyBricksName, Blocks.MOSSY_STONE_BRICKS)
				.withVanillaStairs(Blocks.MOSSY_STONE_BRICK_STAIRS)
				.withVanillaSlab(Blocks.MOSSY_STONE_BRICK_SLAB)
				.withVanillaWall(Blocks.MOSSY_STONE_BRICK_WALL)
				.withVanillaSilverfish(Blocks.INFESTED_MOSSY_STONE_BRICKS)
				.withDepluralizedName(depluralizedMossyBricks));
			
			// chiseled bricks
			// chiseled bricks don't have stairs etc but they do have infested blocks
			String chiseledBricksName = "chiseled_"+bricksName;
			BlockItemHolder chiseledBricks = basicLookupRegistrator.apply(Blocks.CHISELED_STONE_BRICKS, chiseledBricksName);
			BlockItemHolder infestedChiseledBricks = registerBlockWithItem("infested_"+chiseledBricksName,blocks,items,
				props->new InfestedBlock(chiseledBricks.block().get(),props),
				BlockBehaviour.Properties.ofFullCopy(Blocks.INFESTED_CHISELED_STONE_BRICKS));
			vanillaToBlockMap.put(BuiltInRegistries.BLOCK.getKey(Blocks.INFESTED_CHISELED_STONE_BRICKS), infestedChiseledBricks);
			
			// cracked bricks just have infested blocks
			String crackedBricksName = "cracked_"+bricksName;
			BlockItemHolder crackedStoneBricks = basicLookupRegistrator.apply(Blocks.CRACKED_STONE_BRICKS, crackedBricksName);
			BlockItemHolder infestedCrackedBricks = registerBlockWithItem("infested_"+crackedBricksName,blocks,items,
				props->new InfestedBlock(crackedStoneBricks.block().get(),props),
				BlockBehaviour.Properties.ofFullCopy(Blocks.INFESTED_CRACKED_STONE_BRICKS));
			vanillaToBlockMap.put(BuiltInRegistries.BLOCK.getKey(Blocks.INFESTED_CRACKED_STONE_BRICKS), infestedCrackedBricks);
			
			// smooth stone and smooth stone slab have no other variants
			String smoothStoneName = "smooth_"+rockName;
			basicLookupRegistrator.apply(Blocks.SMOOTH_STONE, smoothStoneName);
			String smoothStoneSlabName = smoothStoneName+"_slab";
			BlockItemHolder smoothStoneSlab = registerBlockWithItem(smoothStoneSlabName,blocks,items,
				SlabBlock::new,
				BlockBehaviour.Properties.ofFullCopy(Blocks.SMOOTH_STONE_SLAB));
			vanillaToBlockMap.put(BuiltInRegistries.BLOCK.getKey(Blocks.SMOOTH_STONE_SLAB), smoothStoneSlab);
			
			// ores (same registry order as vanilla)
			// iron and gold can be regular blocks since they don't drop xp
			String goldOreName = rockName + "_gold_ore";
			basicLookupRegistrator.apply(Blocks.GOLD_ORE, goldOreName);
			String ironOreName = rockName + "_iron_ore";
			basicLookupRegistrator.apply(Blocks.IRON_ORE, ironOreName);
			String coalOreName = rockName + "_coal_ore";
			BlockItemHolder coalOre = registerBlockWithItem(coalOreName,blocks,items,
				props -> new DropExperienceBlock(UniformInt.of(0, 2), props),
				BlockBehaviour.Properties.ofFullCopy(Blocks.COAL_ORE));
			vanillaToBlockMap.put(BuiltInRegistries.BLOCK.getKey(Blocks.COAL_ORE), coalOre);
			String lapisOreName = rockName + "_lapis_ore";
			BlockItemHolder lapisOre = registerBlockWithItem(lapisOreName,blocks,items,
				props -> new DropExperienceBlock(UniformInt.of(2, 5),props),
				BlockBehaviour.Properties.ofFullCopy(Blocks.LAPIS_ORE));
			vanillaToBlockMap.put(BuiltInRegistries.BLOCK.getKey(Blocks.LAPIS_ORE), lapisOre);
			String diamondOreName = rockName + "_diamond_ore";
			BlockItemHolder diamondOre = registerBlockWithItem(diamondOreName,blocks,items,
				props -> new DropExperienceBlock(UniformInt.of(3, 7), props),
				BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE));
			vanillaToBlockMap.put(BuiltInRegistries.BLOCK.getKey(Blocks.DIAMOND_ORE), diamondOre);
			String redstoneOreName = rockName + "_redstone_ore";
			BlockItemHolder redstoneOre = registerBlockWithItem(redstoneOreName,blocks,items,
				RedStoneOreBlock::new,
				BlockBehaviour.Properties.ofFullCopy(Blocks.REDSTONE_ORE));
			vanillaToBlockMap.put(BuiltInRegistries.BLOCK.getKey(Blocks.REDSTONE_ORE), redstoneOre);
			String emeraldOreName = rockName + "_emerald_ore";
			BlockItemHolder emeraldOre = registerBlockWithItem(emeraldOreName,blocks,items,
				props -> new DropExperienceBlock(UniformInt.of(3, 7), props),
				BlockBehaviour.Properties.ofFullCopy(Blocks.EMERALD_ORE));
			vanillaToBlockMap.put(BuiltInRegistries.BLOCK.getKey(Blocks.EMERALD_ORE), emeraldOre);
			String copperOreName = rockName + "_copper_ore";
			basicLookupRegistrator.apply(Blocks.COPPER_ORE, copperOreName);
			
		});
		
		if (FMLEnvironment.dist == Dist.CLIENT)
		{
			ClientProxy.subscribeClientEvents(modBus, forgeBus);
		}
	}
	
	@SuppressWarnings("deprecation")
	private static <BLOCK extends Block, FISHBLOCK extends Block> void registerBlockWithVariants(
		final Map<ResourceLocation,BlockItemHolder> vanillaToRockMap,
		final DeferredRegister.Blocks blocks,
		final DeferredRegister.Items items,
		BlockRegistrationData data)
	{

		String baseBlockName = data.getRockBlockName();
		String depluralizedName = data.getDepluralizedName();
		Block vanillaBlock = data.getVanillaBlock();
		BiFunction<String, Supplier<? extends Block>, DeferredItem<BlockItem>> blockItemFactory =
			(name,blockGetter) -> items.registerItem(name, props -> new BlockItem(blockGetter.get(), props.useBlockDescriptionPrefix()));
		ResourceLocation vanillaBlockID = BuiltInRegistries.BLOCK.getKey(vanillaBlock);
		BlockItemHolder block = registerBlockWithItem(baseBlockName,blocks,items,Block::new, BlockBehaviour.Properties.ofFullCopy(vanillaBlock));
		vanillaToRockMap.put(vanillaBlockID, block);
		
		// register the craftable blocks -- stairs, slabs, walls, pressure plates, buttons
		// stairs copy properties from the base block
		String stairsBlockName = depluralizedName+"_stairs";
		DeferredBlock<StairBlock> stairsBlock = blocks.registerBlock(stairsBlockName,
			props -> new StairBlock(block.block().get().defaultBlockState(), props),
			BlockBehaviour.Properties.ofFullCopy(vanillaBlock));
		DeferredItem<BlockItem> stairsBlockItem = blockItemFactory.apply(stairsBlockName, stairsBlock);
		data.getVanillaStairsBlock().ifPresent(vanillaStairs->vanillaToRockMap.put(BuiltInRegistries.BLOCK.getKey(vanillaStairs), new BlockItemHolder(stairsBlock,stairsBlockItem)));
		
		// stoney slabs always have the same properties
		String slabBlockName = depluralizedName+"_slab";
		DeferredBlock<SlabBlock> slabBlock = blocks.registerBlock(slabBlockName, SlabBlock::new,
			BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
		DeferredItem<BlockItem> slabBlockItem = blockItemFactory.apply(slabBlockName, slabBlock);
		data.getVanillaSlabBlock().ifPresent(vanillaSlab->vanillaToRockMap.put(BuiltInRegistries.BLOCK.getKey(vanillaSlab), new BlockItemHolder(slabBlock, slabBlockItem)));
		
		// walls copy properties from the base block
		String wallBlockName = depluralizedName+"_wall";
		DeferredBlock<WallBlock> wallBlock = blocks.registerBlock(wallBlockName, WallBlock::new, 
			BlockBehaviour.Properties.ofLegacyCopy(vanillaBlock).forceSolidOn());
		DeferredItem<BlockItem> wallBlockItem = blockItemFactory.apply(wallBlockName, wallBlock);
		data.getVanillaWallBlock().ifPresent(vanillaWall->vanillaToRockMap.put(BuiltInRegistries.BLOCK.getKey(vanillaWall), new BlockItemHolder(wallBlock, wallBlockItem)));
		
		// buttons always use the same properties
		String buttonBlockName = depluralizedName+"_button";
		DeferredBlock<ButtonBlock> buttonBlock = blocks.registerBlock(buttonBlockName,
			props -> new ButtonBlock(BlockSetType.STONE, 20, props),
			BlockBehaviour.Properties.of().noCollission().strength(0.5F).pushReaction(PushReaction.DESTROY));
		DeferredItem<BlockItem> buttonBlockItem = blockItemFactory.apply(buttonBlockName, buttonBlock);
		data.getVanillaButtonBlock().ifPresent(vanillaButton->vanillaToRockMap.put(BuiltInRegistries.BLOCK.getKey(vanillaButton), new BlockItemHolder(buttonBlock, buttonBlockItem)));
		
		// stone pressure plates use the same properties (except for the color, do we want to make rocks not always be gray?)
		String pressurePlateBlockName = depluralizedName+"_pressure_plate";
		DeferredBlock<PressurePlateBlock> pressurePlateBlock = blocks.registerBlock(pressurePlateBlockName,
			props -> new PressurePlateBlock(BlockSetType.STONE, props),
			BlockBehaviour.Properties.of()
	            .mapColor(MapColor.STONE)
	            .forceSolidOn()
	            .instrument(NoteBlockInstrument.BASEDRUM)
	            .noCollission()
	            .strength(0.5F)
	            .pushReaction(PushReaction.DESTROY));
		DeferredItem<BlockItem> pressurePlateBlockItem = blockItemFactory.apply(pressurePlateBlockName, pressurePlateBlock);
		data.getVanillaPressurePlateBlock().ifPresent(vanillaPlate->vanillaToRockMap.put(BuiltInRegistries.BLOCK.getKey(vanillaPlate), new BlockItemHolder(pressurePlateBlock, pressurePlateBlockItem)));

		// silverfish variant is only registered if a vanilla silverfish variant exists
		data.vanillaSilverfishBlock.ifPresent(vanillaFishBlock->
		{
			String silverfishName = "infested_"+baseBlockName;
			DeferredHolder<Block, InfestedBlock> fishBlock = blocks.registerBlock(silverfishName,
				props -> new InfestedBlock(block.block().get(), props),
				BlockBehaviour.Properties.ofFullCopy(vanillaFishBlock));
			DeferredHolder<Item, BlockItem> fishItem = blockItemFactory.apply(silverfishName, fishBlock);
			vanillaToRockMap.put(vanillaBlockID, new BlockItemHolder(fishBlock, fishItem));
		});
	}
	
	private static <T> DeferredRegister<T> defreg(ResourceKey<Registry<T>> registryKey)
	{
		return defreg(modid -> DeferredRegister.create(registryKey, modid));
	}
	
	private static <T, R extends DeferredRegister<T>> R defreg(Function<String, R> regFactory)
	{
		R register = regFactory.apply(MODID);
		register.register(ModList.get().getModContainerById(MODID).get().getEventBus());
		return register;
	}
	
	private static BlockItemHolder registerBlockWithItem(
		String name,
		DeferredRegister.Blocks blocks,
		DeferredRegister.Items items,
		Function<BlockBehaviour.Properties, Block> blockFactory,
		BlockBehaviour.Properties props)
	{
		DeferredBlock<Block> block = blocks.registerBlock(name, blockFactory, props);
		DeferredItem<BlockItem> item = items.registerItem(name, itemProps -> new BlockItem(block.get(), itemProps.useBlockDescriptionPrefix()));
		return new BlockItemHolder(block, item);
	}

	public static class BlockRegistrationData
	{
		private final String rockBlockName; public String getRockBlockName() { return this.rockBlockName;}
		private final Block vanillaBlock; public Block getVanillaBlock() { return this.vanillaBlock;}
		private Optional<Block> vanillaStairsBlock = Optional.empty(); public Optional<Block> getVanillaStairsBlock() { return this.vanillaStairsBlock;}
		private Optional<Block> vanillaSlabBlock = Optional.empty(); public Optional<Block> getVanillaSlabBlock() { return this.vanillaSlabBlock;}
		private Optional<Block> vanillaWallBlock = Optional.empty(); public Optional<Block> getVanillaWallBlock() { return this.vanillaWallBlock;}
		private Optional<Block> vanillaButtonBlock = Optional.empty(); public Optional<Block> getVanillaButtonBlock() { return this.vanillaButtonBlock;}
		private Optional<Block> vanillaPressurePlateBlock = Optional.empty(); public Optional<Block> getVanillaPressurePlateBlock() { return this.vanillaPressurePlateBlock;}
		private Optional<Block> vanillaSilverfishBlock = Optional.empty(); public Optional<Block> getVanillaSilverfishBlock() { return this.vanillaSilverfishBlock;}
		
		private String depluralizedName; public String getDepluralizedName() { return this.depluralizedName;}
		
		public BlockRegistrationData withVanillaStairs(Block block) {this.vanillaStairsBlock = Optional.of(block);return this;}
		public BlockRegistrationData withVanillaSlab(Block block) {this.vanillaSlabBlock = Optional.of(block);return this;}
		public BlockRegistrationData withVanillaWall(Block block) {this.vanillaWallBlock = Optional.of(block);return this;}
		public BlockRegistrationData withVanillaButton(Block block) {this.vanillaButtonBlock = Optional.of(block);return this;}
		public BlockRegistrationData withVanillaPressurePlate(Block block) {this.vanillaPressurePlateBlock = Optional.of(block);return this;}
		public BlockRegistrationData withVanillaSilverfish(Block block) {this.vanillaSilverfishBlock = Optional.of(block);return this;}
		public BlockRegistrationData withDepluralizedName(String name) {this.depluralizedName = name;return this;}
		
		public BlockRegistrationData(String rockBlockName, Block vanillaBlock)
		{
			this.rockBlockName = rockBlockName;
			this.vanillaBlock = vanillaBlock;
			this.depluralizedName = rockBlockName;
		}
		
	}
}
