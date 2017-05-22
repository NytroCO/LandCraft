package landmaster.landcraft.world;

import java.lang.invoke.*;

import com.google.common.base.*;

import net.minecraft.world.biome.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.registry.*;

public class LandcraftBiomes {
	private static final Biome.BiomeProperties dunans_properties = new Biome.BiomeProperties("dunans");
	
	static {
		dunans_properties.setTemperature(0.4f);
		dunans_properties.setRainfall(1.0f);
		dunans_properties.setWaterColor(0x000099);
		dunans_properties.setHeightVariation(0.3f);
	}
	
	public static final Biome dunans = new DunansBiome(dunans_properties).setRegistryName("dunans");
	
	public static void init() {
		GameRegistry.register(dunans);
		
		try {
			registerBiomeTypeM.invoke(dunans,
					BiomeDictionary.Type.WET,
					BiomeDictionary.Type.MUSHROOM,
					BiomeDictionary.Type.FOREST,
					BiomeDictionary.Type.DENSE);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	// ===============================================================
	
	private static final MethodHandle registerBiomeTypeM;
	static {
		try {
			MethodHandle temp;
			try {
				temp = MethodHandles.lookup().findStatic(BiomeDictionary.class, "addTypes", MethodType.methodType(void.class, Biome.class, BiomeDictionary.Type[].class));
			} catch (NoSuchMethodException e) {
				temp = MethodHandles.lookup().findStatic(BiomeDictionary.class, "registerBiomeType", MethodType.methodType(boolean.class, Biome.class, BiomeDictionary.Type[].class));
			}
			registerBiomeTypeM = temp;
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
}
