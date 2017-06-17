package landmaster.landcraft.world.gen;

import java.util.*;

import landmaster.landcore.api.*;
import landmaster.landcraft.block.*;
import landmaster.landcraft.content.*;
import landmaster.landcraft.util.*;
import landmaster.landcraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.*;
import net.minecraftforge.fml.common.*;

public class LandiaOreWorldgen implements IWorldGenerator {

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator,
			IChunkProvider chunkProvider) {
		if (chunkGenerator instanceof LandiaChunkGenerator) {
			generateOres(world, random, new BlockPos(chunkX*16, 0, chunkZ*16));
		}
	}
	
	// Convenience method
	private static IBlockState oreState(LandiaOreType type) {
		return LandCraftContent.landia_ore.getDefaultState().withProperty(BlockLandiaOre.TYPE, type);
	}
	
	private static void generateOres(World worldIn, Random rand, BlockPos pos) {
		Tools.generateOre(oreState(LandiaOreType.KELLINE), worldIn, rand,
				pos.getX(), pos.getZ(), 6, 24, 3, 7, LandiaOreType.KELLINE.numPerChunk());
		Tools.generateOre(oreState(LandiaOreType.GARFAX), worldIn, rand,
				pos.getX(), pos.getZ(), 6, 60, 3, 7, LandiaOreType.GARFAX.numPerChunk());
		Tools.generateOre(oreState(LandiaOreType.MORGANINE), worldIn, rand,
				pos.getX(), pos.getZ(), 28, 56, 3, 7, LandiaOreType.MORGANINE.numPerChunk());
		Tools.generateOre(oreState(LandiaOreType.RACHELINE), worldIn, rand,
				pos.getX(), pos.getZ(), 9, 35, 3, 7, LandiaOreType.RACHELINE.numPerChunk());
		Tools.generateOre(oreState(LandiaOreType.FRISCION), worldIn, rand,
				pos.getX(), pos.getZ(), 13, 70, 3, 7, LandiaOreType.FRISCION.numPerChunk());
	}
	
}
