package commoble.extrarocks.datagen;

import java.util.Map;

import net.commoble.extrarocks.ExtraRocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class PaletteSwappedBlockDataProvider extends BlockDataProvider
{
	protected final String baseBlockType;
	protected final String rock;
	
	public PaletteSwappedBlockDataProvider(Block block, String baseBlockType, String rock)
	{
		super(block);
		this.baseBlockType = baseBlockType;
		this.rock = rock;
	}

	@Override
	protected void generateBlockModels(Map<ResourceLocation, SimpleModel> blockModels)
	{
		ResourceLocation blockModel = this.reformatBlockID("block/%s");
		ResourceLocation textureLocation = ResourceLocation.withDefaultNamespace("block/" + this.baseBlockType + "/" + ExtraRocks.MODID + "/" + this.rock);
		blockModels.put(blockModel, SimpleModel.create("block/cube_all")
			.addTexture("all", textureLocation));
	}

	
}
