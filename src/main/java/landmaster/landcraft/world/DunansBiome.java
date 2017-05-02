package landmaster.landcraft.world;

import java.util.*;

import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraftforge.fml.relauncher.*;

public class DunansBiome extends Biome {
	
	public DunansBiome(BiomeProperties properties) {
		super(properties);
		this.theBiomeDecorator.treesPerChunk = 6;
		this.theBiomeDecorator.flowersPerChunk = 14;
		this.theBiomeDecorator.mushroomsPerChunk = 8;
		this.theBiomeDecorator.bigMushroomsPerChunk = 4;
	}
	
	@Override
	public void decorate(World worldIn, Random rand, BlockPos pos){
		super.decorate(worldIn, rand, pos);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getSkyColorByTemp(float currentTemperature) {
		return 0x06074F;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getGrassColorAtPos(BlockPos pos) {
		return 0x000099;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getFoliageColorAtPos(BlockPos pos) {
		return 0x000099;
	}
}
