package commoble.extrarocks.datagen;

import java.util.Map;

import com.mojang.math.Quadrant;

import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.level.block.Block;


public class InfestedStoneDataProvider extends InfestedBlockDataProvider
{
	protected final ResourceLocation mirroredBlockModel;
	
	public InfestedStoneDataProvider(Block uninfestedBlock)
	{
		super(uninfestedBlock);
		this.mirroredBlockModel = IDUtil.reformatID(IDUtil.blockId(uninfestedBlock), "block/%s_mirrored");
	}

	@Override
	protected void generateBlockStates(Map<ResourceLocation, BlockModelDefinition> blockstates)
	{
		// use the four-way mirrors like regular stone
		blockstates.put(this.blockId(), BlockStateBuilder.singleVariant(BlockStateBuilder.randomModels(
			new Weighted<>(BlockStateBuilder.model(this.blockModel), 1),
			new Weighted<>(BlockStateBuilder.model(this.mirroredBlockModel), 1),
			new Weighted<>(BlockStateBuilder.model(this.blockModel, Quadrant.R0, Quadrant.R180), 1),
			new Weighted<>(BlockStateBuilder.model(this.mirroredBlockModel, Quadrant.R0, Quadrant.R180), 1))));
	}

}
