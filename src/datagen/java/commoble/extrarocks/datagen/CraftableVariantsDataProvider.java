package commoble.extrarocks.datagen;

import java.util.function.Function;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

/**
 * Generates data for stairs, slabs, walls, pressure plates, and buttons
 */
public class CraftableVariantsDataProvider
{	
	protected final Block baseBlock;
	/** actual ID of the cubic block we are basing the variants on **/
	protected final ResourceLocation baseBlockID;
	
	/** the base block ID, but without plurals, e.g. stone_bricks -> stone_brick (for use with e.g. stone_brick_stairs) **/
	protected final ResourceLocation depluralizedBlockID;
	
	/** The texture to use for the models (all models use a single texture)**/
	protected final ResourceLocation textureLocation;
	
	public CraftableVariantsDataProvider(Block baseBlock, ResourceLocation depluralizedBlockID, ResourceLocation textureLocation)
	{
		this.baseBlock = baseBlock;
		this.baseBlockID = IDUtil.blockId(baseBlock);
		this.depluralizedBlockID = depluralizedBlockID;
		this.textureLocation = textureLocation;
	}

	public void run(DataGenContext context)
	{
		Function<String,Block> depluralizeToBlock = formatString ->
			BuiltInRegistries.BLOCK.getValue(IDUtil.reformatID(this.depluralizedBlockID, formatString));
		Block stairsBlock = depluralizeToBlock.apply("%s_stairs");
		new StairsDataProvider(stairsBlock, this.baseBlock, this.textureLocation).run(context);	
		Block slabBlock = depluralizeToBlock.apply("%s_slab");
		new SlabDataProvider(slabBlock, this.baseBlock, this.textureLocation, this.textureLocation, false).run(context);
		Block wallBlock = depluralizeToBlock.apply("%s_wall");
		new WallDataProvider(wallBlock, this.baseBlock, this.textureLocation).run(context);
		Block plateBlock = depluralizeToBlock.apply("%s_pressure_plate");
		new PressurePlateDataProvider(plateBlock, this.baseBlock, this.textureLocation).run(context);
		Block buttonBlock = depluralizeToBlock.apply("%s_button");
		new ButtonDataProvider(buttonBlock, this.baseBlock, this.textureLocation).run(context);
	}
}
