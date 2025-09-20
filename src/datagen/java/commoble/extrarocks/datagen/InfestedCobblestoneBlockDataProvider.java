package commoble.extrarocks.datagen;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

public class InfestedCobblestoneBlockDataProvider extends InfestedBlockDataProvider
{

	public InfestedCobblestoneBlockDataProvider(Block uninfestedBlock)
	{
		super(uninfestedBlock);
	}

	@Override
	protected void generateTags(TagProvider<Block> blocks, TagProvider<Item> items)
	{
		super.generateTags(blocks, items);
		blocks.tag(Tags.Blocks.COBBLESTONES_INFESTED).add(this.key());
		items.tag(Tags.Items.COBBLESTONES_INFESTED).add(this.itemKey());
	}
}
