package commoble.extrarocks.datagen;

import net.commoble.extrarocks.ExtraRocks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

public class IDUtil
{
	/**
	 * Reformats an ID's path according to a format string, keeping the namespace
	 * @param id The ID to reformat
	 * @param pathFormat A format string to apply String.format to, containing a single %s argument to be replaced with the original ID's path string
	 * @return A reformatted ID with the original ID's path applied to the path format string
	 */
	public static ResourceLocation reformatID(ResourceLocation id, String pathFormat)
	{
		return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), String.format(pathFormat, id.getPath()));
	}
	
	/**
	 * Reformats an ID's path according to the given pathformat and args. The ID's path is not implicitly included as an argument.
	 * @param id The ID to reformat
	 * @param pathFormat a format string to be given to String.format
	 * @param args Arguments to be given to String.format
	 * @return The reformatted ID, retaining the same namespace as the original
	 */
	public static ResourceLocation reformatID(ResourceLocation id, String pathFormat, Object... args)
	{
		return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), String.format(pathFormat, args));
	}
	
	public static ResourceLocation makePermutationTextureLocation(String vanillaBlockName, String rock)
	{
		return ResourceLocation.withDefaultNamespace(String.format("block/%s/%s/%s", vanillaBlockName, ExtraRocks.MODID, rock));
	}
	
	@SuppressWarnings("deprecation")
	public static ResourceLocation blockId(Block block)
	{
		return block.builtInRegistryHolder().getKey().location();
	}
	
	public static String blockPath(Block block)
	{
		return blockId(block).getPath();
	}
}
