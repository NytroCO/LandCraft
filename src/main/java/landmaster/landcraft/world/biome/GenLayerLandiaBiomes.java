package landmaster.landcraft.world.biome;

import java.util.*;

import com.google.common.collect.*;

import net.minecraft.util.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.layer.*;
import net.minecraftforge.common.BiomeManager.BiomeEntry;

public class GenLayerLandiaBiomes extends GenLayer {
	protected static final List<BiomeEntry> allowedBiomes = ImmutableList.of(
			new BiomeEntry(LandcraftBiomes.dunans, 20),
			new BiomeEntry(LandcraftBiomes.tunis, 13)
			);
	
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
				childInts[j + i * areaWidth] = Biome.getIdForBiome(
						WeightedRandom.getRandomItem(new Random(this.nextLong(1L << 48)), allowedBiomes).biome);
			}
		}
		
		return childInts;
	}
}
