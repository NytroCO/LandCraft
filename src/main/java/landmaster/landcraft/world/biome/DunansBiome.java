package landmaster.landcraft.world.biome;

import net.minecraft.util.math.*;
import net.minecraftforge.fml.relauncher.*;

class DunansBiome extends LandiaBiome {
	public DunansBiome(BiomeProperties properties) {
		super(properties);
		this.decorator.treesPerChunk = 6;
		this.decorator.flowersPerChunk = 14;
		this.decorator.mushroomsPerChunk = 8;
		this.decorator.bigMushroomsPerChunk = 4;
		this.decorator.waterlilyPerChunk = 4;
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
