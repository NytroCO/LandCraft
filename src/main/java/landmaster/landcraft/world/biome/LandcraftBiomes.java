package landmaster.landcraft.world.biome;

import landmaster.landcraft.api.*;
import net.minecraft.world.biome.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.eventhandler.*;

@Mod.EventBusSubscriber(modid = ModInfo.MODID)
public class LandcraftBiomes {
	private static final Biome.BiomeProperties dunans_properties = new Biome.BiomeProperties("dunans");
	
	static {
		dunans_properties.setTemperature(0.4f);
		dunans_properties.setRainfall(1.0f);
		dunans_properties.setWaterColor(0x000099);
		dunans_properties.setHeightVariation(0.3f);
	}
	
	public static final Biome dunans = new DunansBiome(dunans_properties).setRegistryName("dunans");
	
	private static final Biome.BiomeProperties tunis_properties = new Biome.BiomeProperties("tunis");
	
	static {
		tunis_properties.setTemperature(0.85f);
		tunis_properties.setRainfall(0.0f);
		tunis_properties.setRainDisabled();
		tunis_properties.setWaterColor(0x23E1FF);
		tunis_properties.setHeightVariation(0.13f);
	}
	
	public static final Biome tunis = new TunisBiome(tunis_properties).setRegistryName("tunis");
	
	@SubscribeEvent
	public static void init(RegistryEvent.Register<Biome> event) {
		event.getRegistry().registerAll(dunans, tunis);
		
		BiomeDictionary.addTypes(dunans,
				BiomeDictionary.Type.WET,
				BiomeDictionary.Type.MUSHROOM,
				BiomeDictionary.Type.FOREST,
				BiomeDictionary.Type.DENSE);
		
		BiomeDictionary.addTypes(tunis,
				BiomeDictionary.Type.DRY);
	}
}
