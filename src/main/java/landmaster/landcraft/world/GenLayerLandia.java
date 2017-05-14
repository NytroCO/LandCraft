package landmaster.landcraft.world;

import net.minecraft.world.*;
import net.minecraft.world.gen.layer.*;

public abstract class GenLayerLandia extends GenLayer {
	public GenLayerLandia(long seed) {
		super(seed);
	}
	
	public static GenLayer[] initializeAllBiomeGenerators(long seed, WorldType type) {
		int biomeSize = type == WorldType.LARGE_BIOMES ? 6 : 4;
		biomeSize = getModdedBiomeSize(type, biomeSize);
		
		GenLayer genLayer = new GenLayerIsland(1L);
		genLayer = new GenLayerFuzzyZoom(2000L, genLayer);
		genLayer = new GenLayerLandiaBiomes(200L, genLayer);
		genLayer = GenLayerZoom.magnify(2000L, genLayer, biomeSize);
		genLayer = GenLayerZoom.magnify(1000L, genLayer, biomeSize / 2);
		GenLayer genLayerVoronoiZoom = new GenLayerVoronoiZoom(10L, genLayer);
		
		genLayer.initWorldGenSeed(seed);
		genLayerVoronoiZoom.initWorldGenSeed(seed);
		
		return new GenLayer[] { genLayer, genLayerVoronoiZoom };
	}
}
