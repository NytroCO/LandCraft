package landmaster.landcraft.world;

import net.minecraft.world.biome.*;
import net.minecraft.world.gen.layer.*;

public class GenLayerLandiaBiomes extends GenLayer {
	
	protected Biome[] allowedBiomes = { LandcraftBiomes.dunans };
	
	public GenLayerLandiaBiomes(long seed, GenLayer parentIn) {
		super(seed);
		parent = parentIn;
	}
	
	@Override
	public int[] getInts(int areaX, int areaY, int areaWidth, int areaHeight) {
		// int[] parentInts = parent.getInts(areaX, areaY, areaWidth,
		// areaHeight);
		int[] childInts = IntCache.getIntCache(areaWidth * areaHeight);
		
		for (int i = 0; i < areaHeight; ++i) {
			for (int j = 0; j < areaWidth; ++j) {
				initChunkSeed((long) (j + areaX), (long) (i + areaY));
				childInts[j + i * areaWidth] = Biome.getIdForBiome(allowedBiomes[nextInt(allowedBiomes.length)]);
			}
		}
		
		return childInts;
	}
}
