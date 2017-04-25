package landmaster.landcraft.world;

import java.lang.reflect.*;

import com.google.common.base.*;

import net.minecraft.world.biome.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.registry.*;

public class LandcraftBiomes {
	private static final Biome.BiomeProperties dunans_properties = new Biome.BiomeProperties("dunans");
	
	static {
		dunans_properties.setTemperature(0.0f);
		dunans_properties.setSnowEnabled();
		dunans_properties.setRainfall(1.0f);
		dunans_properties.setWaterColor(0x000099);
	}
	
	public static final Biome dunans = new DunansBiome(dunans_properties).setRegistryName("dunans");
	
	public static void init() {
		GameRegistry.register(dunans);
		
		try {
			registerBiomeTypeM.invoke(null, dunans,
					new BiomeDictionary.Type[] {BiomeDictionary.Type.COLD, BiomeDictionary.Type.WET, BiomeDictionary.Type.SNOWY});
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	private static final Method registerBiomeTypeM;
	static {
		try {
			Method temp;
			try {
				temp = BiomeDictionary.class.getMethod("addTypes", Biome.class, BiomeDictionary.Type[].class);
			} catch (NoSuchMethodException e) {
				temp = BiomeDictionary.class.getMethod("registerBiomeType", Biome.class, BiomeDictionary.Type[].class);
			}
			registerBiomeTypeM = temp;
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
}
