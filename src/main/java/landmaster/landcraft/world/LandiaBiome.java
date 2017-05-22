package landmaster.landcraft.world;

import java.util.*;

import landmaster.landcraft.config.*;
import landmaster.landcraft.content.*;
import net.minecraft.block.material.*;
import net.minecraft.block.state.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;

public class LandiaBiome extends Biome {
	public LandiaBiome(BiomeProperties properties) {
		super(properties);
	}
	
	@Override
	public void decorate(World worldIn, Random rand, BlockPos pos) {
		super.decorate(worldIn, rand, pos);
		genPlantNormally(worldIn, rand, pos, LandCraftContent.wild_onion.getDefaultState(), Config.onion_per_chunk, Material.GRASS);
		genPlantNormally(worldIn, rand, pos, LandCraftContent.wild_rice.getDefaultState(), Config.rice_per_chunk, Material.GRASS);
	}
	
	protected static void genPlantNormally(World world, Random random, BlockPos pos, IBlockState state, int amount, Material blockBelow) {
		for (int i = 0; i < amount; i++) {
			if (random.nextInt(40) == 0) {
				BlockPos randomPos = new BlockPos(pos.getX() + random.nextInt(16) + 8, 0,
						pos.getZ() + random.nextInt(16) + 8);
				randomPos = world.getTopSolidOrLiquidBlock(randomPos);
				
				if (world.getBlockState(randomPos.down()).getMaterial() == blockBelow) {
					if (state.getBlock().canPlaceBlockAt(world, randomPos) && world.isAirBlock(randomPos)) {
						world.setBlockState(randomPos, state, 2);
					}
				}
			}
		}
	}
}
