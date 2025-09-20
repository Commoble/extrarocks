package net.commoble.extrarocks;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public enum RockType implements StringRepresentable
{
	DOIRITE("doirite"),
	GABBRO("gabbro"),
	GRAANITE("graanite"),
	ANDEEZITE("andeezite"),
	BAASALT("baasalt"),
	RHYOLITE("rhyolite"),
	GNEISS("gneiss"),
	MARBLE("marble"),
	PHYLLITE("phyllite"),
	SCHIST("schist"),
	SLATE("slate"),
	CHERT("chert"),
	DOLOMITE("dolomite"),
	HALITE("halite"),
	LIMESTONE("limestone"),
	SHALE("shale");
	
	public static final RockType[] PLUTONIC_ROCKS =
	{
		RockType.DOIRITE,
		RockType.GABBRO,
		RockType.GRAANITE
	};
	
	public static final RockType[] VOLCANIC_ROCKS =
	{
		RockType.ANDEEZITE,
		RockType.BAASALT,
		RockType.RHYOLITE
	};
	
	public static final RockType[] METAMORPHIC_ROCKS =
	{
		RockType.GNEISS,
		RockType.MARBLE,
		RockType.PHYLLITE,
		RockType.SCHIST,
		RockType.SLATE
	};
	
	public static final RockType[] SEDIMENTARY_ROCKS =
	{
		RockType.CHERT,
		RockType.DOLOMITE,
		RockType.HALITE,
		RockType.LIMESTONE,
		RockType.SHALE
	};
	
	public static final RockType[][] ROCK_LISTS = Util.make(() ->
	{
		RockType[][] rockLists = new RockType[4][];
		rockLists[GeologyType.PLUTONIC.ordinal()] = PLUTONIC_ROCKS;
		rockLists[GeologyType.VOLCANIC.ordinal()] = VOLCANIC_ROCKS;
		rockLists[GeologyType.METAMORPHIC.ordinal()] = METAMORPHIC_ROCKS;
		rockLists[GeologyType.SEDIMENTARY.ordinal()] = SEDIMENTARY_ROCKS;
		
		return rockLists;
	});
	
	/** The inner maps here are populated during mod construction **/
	public static final Map<RockType,Map<ResourceLocation,BlockItemHolder>> ROCK_TO_VANILLA_TO_ROCK_BLOCK = Util.make(() ->
	{
		Map<RockType,Map<ResourceLocation,BlockItemHolder>> map = new EnumMap<>(RockType.class);
		for (RockType rock : RockType.values())
		{
			Map<ResourceLocation,BlockItemHolder> vanillaToRockMap = new HashMap<>();
			map.put(rock, vanillaToRockMap);
		}
		return map;
	});
	
	private final String rockName;
	
	RockType(String name)
	{
		this.rockName = name;
	}

	@Override
	public String getSerializedName()
	{
		return this.rockName;
	}
}
