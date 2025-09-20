package net.commoble.extrarocks.client;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import net.commoble.extrarocks.RockType;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientProxy
{
	public static void subscribeClientEvents(IEventBus modBus, IEventBus forgeBus)
	{
		modBus.addListener(ClientProxy::onClientSetup);
	}
	
	@SuppressWarnings("deprecation")
	private static void onClientSetup(FMLClientSetupEvent event)
	{
		// some of our rock blocks need to have a non-solid render type
		// we can use a vanilla block ID to retrieve the ones we need to set the render type for
		ChunkSectionLayer translucent = ChunkSectionLayer.TRANSLUCENT;
		final List<ResourceLocation> vanillaBlockBasesNeedingTranslucent = ImmutableList.of(
			Blocks.GOLD_ORE,
			Blocks.IRON_ORE,
			Blocks.COAL_ORE,
			Blocks.LAPIS_ORE,
			Blocks.DIAMOND_ORE,
			Blocks.REDSTONE_ORE,
			Blocks.EMERALD_ORE,
			Blocks.COPPER_ORE)
			.stream().map(BuiltInRegistries.BLOCK::getKey).collect(Collectors.toList());
		
		RockType.ROCK_TO_VANILLA_TO_ROCK_BLOCK.forEach((rockType, vanillaMapper) ->
			vanillaBlockBasesNeedingTranslucent.forEach(vanillaBlockID ->
				ItemBlockRenderTypes.setRenderLayer(vanillaMapper.get(vanillaBlockID).block().get(), translucent)));
	}
}
