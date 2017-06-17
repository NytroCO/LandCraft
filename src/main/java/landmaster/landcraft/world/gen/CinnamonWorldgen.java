package landmaster.landcraft.world.gen;

import java.util.*;

import com.google.common.collect.*;

import landmaster.landcraft.content.*;
import landmaster.landcraft.util.*;
import landmaster.landcraft.world.*;
import landmaster.landcraft.world.biome.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.*;
import net.minecraftforge.fml.common.*;

public class CinnamonWorldgen implements IWorldGenerator {
	protected static final LandiaTreeGenerator TREE_GEN = new LandiaTreeGenerator(9, 7,
			LandCraftContent.landia_log.getDefaultState().withProperty(LandiaTreeType.L_TYPE, LandiaTreeType.CINNAMON),
			LandCraftContent.landia_leaves.getDefaultState().withProperty(LandiaTreeType.L_TYPE,
					LandiaTreeType.CINNAMON));
	
	protected static final Set<Biome> allowedBiomes = ImmutableSet.of(LandcraftBiomes.dunans);
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {
		if (chunkGenerator instanceof LandiaChunkGenerator) {
			BlockPos pos = new BlockPos(chunkX*16+8, 0, chunkZ*16+8);
			if (allowedBiomes.contains(world.getBiome(pos))) {
				int xSpawn = pos.getX() + random.nextInt(16);
				int ySpawn = 117;
				int zSpawn = pos.getZ() + random.nextInt(16);
				BlockPos position = new BlockPos(xSpawn, ySpawn, zSpawn);
				
				TREE_GEN.generateTree(random, world, position);
			}
		}
	}
	
}
