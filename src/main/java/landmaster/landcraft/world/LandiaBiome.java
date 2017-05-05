package landmaster.landcraft.world;

import java.util.*;

import landmaster.landcore.api.*;
import landmaster.landcraft.*;
import landmaster.landcraft.block.*;
import landmaster.landcraft.util.*;
import net.minecraft.block.state.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;

public class LandiaBiome extends Biome {
	public LandiaBiome(BiomeProperties properties) {
		super(properties);
	}
	
	// Convenience method
	private static IBlockState oreState(LandiaOreType type) {
		return LandCraft.landia_ore.getDefaultState().withProperty(BlockLandiaOre.TYPE, type);
	}
	
	@Override
	public void decorate(World worldIn, Random rand, BlockPos pos){
		super.decorate(worldIn, rand, pos);
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
