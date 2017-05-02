package landmaster.landcraft.world;

import net.minecraft.world.biome.*;
import net.minecraft.world.gen.layer.*;

public class GenLayerBiomesL extends GenLayer {

	protected Biome[] allowedBiomes = {LandcraftBiomes.dunans};

	public GenLayerBiomesL(long seed, GenLayer genlayer) {
		super(seed);
		parent = genlayer;
	}

	public GenLayerBiomesL(long seed) {
		super(seed);
	}

	@Override
	public int[] getInts(int x, int z, int width, int depth)
	{
		int[] dest = IntCache.getIntCache(width*depth);

		for (int dz=0; dz<depth; dz++)
			for (int dx=0; dx<width; dx++)
			{
				initChunkSeed(dx+x, dz+z);
				dest[dx+dz*width] = Biome.getIdForBiome(allowedBiomes[nextInt(allowedBiomes.length)]);
			}
		return dest;
	}
}
