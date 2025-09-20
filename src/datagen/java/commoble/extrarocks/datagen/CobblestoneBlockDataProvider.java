package commoble.extrarocks.datagen;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public class CobblestoneBlockDataProvider extends PaletteSwappedBlockDataProvider
{

	public CobblestoneBlockDataProvider(Block block, String rock)
	{
		super(block, "cobblestone", rock);
	}

	@Override
	protected void generateTags(TagProvider<Block> blocks, TagProvider<Item> items)
	{
		super.generateTags(blocks, items);
		blocks.tag(Tags.Blocks.COBBLESTONES_NORMAL).add(this.key());
		items.tag(ExtraRocksDatagen.EXTRA_COBBLESTONES).add(this.itemKey());
	}

	
}
