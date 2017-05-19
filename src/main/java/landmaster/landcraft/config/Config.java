package landmaster.landcraft.config;

import org.apache.commons.lang3.*;

import landmaster.landcraft.util.*;
import net.minecraftforge.common.config.*;
import net.minecraftforge.fml.common.event.*;

public class Config extends Configuration {
	public static boolean breeder;
	public static boolean player_mime;
	public static boolean thorium_generator;
	public static boolean wrench;
	public static boolean pot;
	
	public static int onion_per_chunk, rice_per_chunk, rice_per_chunk_water;
	
	public static int landiaDimensionID;
	
	public Config(FMLPreInitializationEvent event) {
		super(event.getSuggestedConfigurationFile());
	}
	
	public void sync() {
		breeder = getBoolean("Enable Breeder Reactor", "machines", true, "Enable Breeder Reactor");
		player_mime = getBoolean("Enable Player Mime", "machines", true, "Enable Player Mime");
		thorium_generator = getBoolean("Enable Thorium Generator", "machines", true, "Enable Thorium Generator");
		pot = getBoolean("Enable Pot", "machines", true, "Enable Pot");
		
		wrench = getBoolean("Enable Wrench", "tools", true, "Enable Wrench");
		
		onion_per_chunk = getInt("Wild onion per chunk", "naturegen", 8, 0, 256, "Amount of attempted wild onion generation in Landia");
		rice_per_chunk = getInt("Wild rice per chunk on land", "naturegen", 8, 0, 256, "Amount of attempted wild rice generation on land in Landia");
		rice_per_chunk_water = getInt("Wild rice per chunk on water", "naturegen", 13, 0, 256, "Amount of attempted wild rice generation on water in Landia");
		
		landiaDimensionID = getInt("Dimension ID of Landia", "dimensions", 1304, Integer.MIN_VALUE, Integer.MAX_VALUE, "Dimension ID of Landia");
		
		for (LandiaOreType type: LandiaOreType.values()) {
			type.setNumPerChunk(getInt(StringUtils.capitalize(type.toString())+" veins per chunk",
					"oregen", type.numPerChunk(), 0, Integer.MAX_VALUE,
					StringUtils.capitalize(type.toString())+" veins per chunk"));
		}
		
		if (hasChanged()) save();
	}
}
