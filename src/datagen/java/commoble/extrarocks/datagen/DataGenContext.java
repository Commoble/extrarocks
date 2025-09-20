package commoble.extrarocks.datagen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.commoble.extrarocks.ExtraRocks;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public record DataGenContext(
	GatherDataEvent event,
	Map<ResourceLocation, BlockModelDefinition> blockStates,
	Map<ResourceLocation, ClientItem> items,
	Map<ResourceLocation, SimpleModel> models,
	Map<ResourceLocation, LootTable> lootTables,
	Map<ResourceLocation, Recipe<?>> recipes,
	TagProvider<Block> blockTags,
	TagProvider<Item> itemTags,
	LanguageProvider lang,
	List<SpriteSource> blocksAtlas
	)
{
	public static DataGenContext of(GatherDataEvent event)
	{
		return new DataGenContext(event, new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(),
			TagProvider.create(event, Registries.BLOCK),
			TagProvider.create(event, Registries.ITEM),
			new LanguageProvider(event.getGenerator().getPackOutput(), ExtraRocks.MODID, "en_us") {
				@Override
				protected void addTranslations()
				{
					// no
				}
			},
			new ArrayList<>());
	}
}
