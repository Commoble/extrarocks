package commoble.extrarocks.datagen;

import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;

@SuppressWarnings("deprecation")
public class BlockDataProvider
{
	public final Block block;
	
	public BlockDataProvider(Block block)
	{
		this.block = block;
	}
	
	public ResourceKey<Block> key()
	{
		return this.block.builtInRegistryHolder().getKey();
	}
	
	public ResourceKey<Item> itemKey()
	{
		return ResourceKey.create(Registries.ITEM, this.blockId());
	}
	
	public ResourceLocation blockId()
	{
		return this.key().location();
	}

	public void run(DataGenContext context)
	{
		// generate blockstates
		this.generateBlockStates(context.blockStates());
		
		// block models
		this.generateBlockModels(context.models());
		
		// item models
		this.generateClientItems(context.items());
		
		// loot tables
		this.generateLootTables(context);
		
		// recipes
		this.generateRecipes(context.recipes());
		
		this.generateTags(context.blockTags(), context.itemTags());
		
		context.lang().addBlock(() -> this.block, WordUtils.capitalize(this.blockId().getPath().replace('_', ' ')));
	}
	
	protected void generateBlockStates(Map<ResourceLocation, BlockModelDefinition> blockStates)
	{
		ResourceLocation blockModel = this.reformatBlockID("block/%s");
		blockStates.put(this.blockId(), BlockStateBuilder.singleVariant(BlockStateBuilder.model(blockModel)));
	}
	
	protected void generateBlockModels(Map<ResourceLocation, SimpleModel> blockModels)
	{
		ResourceLocation blockModel = this.reformatBlockID("block/%s");
		ResourceLocation textureLocation = blockModel;
		blockModels.put(blockModel, SimpleModel.create("block/cube_all")
			.addTexture("all", textureLocation));
	}
	
	protected void generateClientItems(Map<ResourceLocation, ClientItem> clientItems)
	{
		clientItems.put(this.blockId(), SimpleModel.blockModelWrapper(this.reformatBlockID("block/%s")));
	}
	
	protected void generateLootTables(DataGenContext context)
	{
		ResourceLocation lootLocation = this.reformatBlockID("blocks/%s");
		Item item = BuiltInRegistries.ITEM.getValue(this.blockId());
		context.lootTables().put(lootLocation, LootTable.lootTable()
			.setParamSet(LootContextParamSets.BLOCK)
			.setRandomSequence(lootLocation)
			.withPool(LootPool.lootPool()
				.add(LootItem.lootTableItem(item))
				.when(ExplosionCondition.survivesExplosion()))
			.build());
	}
	
	protected void generateRecipes(Map<ResourceLocation,Recipe<?>> recipes)
	{
		
	}
	
	protected void generateTags(TagProvider<Block> blocks, TagProvider<Item> items)
	{
		blocks.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(this.key());
	}
	
	/**
	 * Creates a new resourcelocation from the base rock ID, using the same namespace, and inserting the base rock path
	 * into a given single-argument format string
	 * @param formatString A single-argument format string (with argument %s) to give to String.format 
	 * @return A new resourcelocation with base rock id's namespace, and with the base rock id's path inserted into the given format string as the new path
	 */
	protected ResourceLocation reformatBlockID(String formatString)
	{
		return ResourceLocation.fromNamespaceAndPath(this.blockId().getNamespace(), String.format(formatString, this.blockId().getPath()));
	}
}
