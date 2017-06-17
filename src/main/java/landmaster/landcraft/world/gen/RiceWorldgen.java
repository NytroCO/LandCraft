package landmaster.landcraft.world.gen;

import java.util.*;

import com.google.common.collect.*;

import landmaster.landcraft.config.*;
import landmaster.landcraft.content.*;
import landmaster.landcraft.world.biome.*;
import net.minecraft.block.state.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.*;

public class RiceWorldgen extends LandiaPlantWorldgen {
	
	@Override
	protected IBlockState getState() {
		return LandCraftContent.wild_rice.getDefaultState();
	}
	
	@Override
	protected int getAmount() {
		return Config.rice_per_chunk;
	}

	@Override
	protected long randomUniquifier() {
		return 2953L;
	}
	
	protected static final Set<Biome> allowedBiomes = ImmutableSet.of(LandcraftBiomes.dunans);
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {
		BlockPos pos = new BlockPos(chunkX*16+8, 0, chunkZ*16+8);
		if (allowedBiomes.contains(world.getBiome(pos))) {
			super.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
		}
	}
}
