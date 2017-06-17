package landmaster.landcraft.world.gen;

import java.util.*;

import landmaster.landcraft.world.*;
import net.minecraft.block.material.*;
import net.minecraft.block.state.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.*;
import net.minecraftforge.fml.common.*;

public abstract class LandiaPlantWorldgen implements IWorldGenerator {
	protected abstract IBlockState getState();
	protected abstract int getAmount();
	protected abstract long randomUniquifier();
	protected boolean isBlockBelowSuitable(IBlockState state) {
		return state.getMaterial() == Material.GRASS;
	}
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {
		if (chunkGenerator instanceof LandiaChunkGenerator) {
			this.genPlantNormally(world, random, new BlockPos(chunkX*16, 0, chunkZ*16), getState(), getAmount());
		}
	}
	
	protected void genPlantNormally(World world, Random random, BlockPos pos, IBlockState state, int amount) {
		Random frandom = new Random(random.nextLong() ^ this.randomUniquifier());
		for (int i = 0; i < amount; i++) {
			if (frandom.nextInt(40) == 0) {
				BlockPos randomPos = new BlockPos(pos.getX() + frandom.nextInt(16) + 8, 0,
						pos.getZ() + frandom.nextInt(16) + 8);
				randomPos = world.getTopSolidOrLiquidBlock(randomPos);
				
				if (isBlockBelowSuitable(world.getBlockState(randomPos.down()))) {
					if (state.getBlock().canPlaceBlockAt(world, randomPos) && world.isAirBlock(randomPos)) {
						world.setBlockState(randomPos, state, 2);
					}
				}
			}
		}
	}
	
}
