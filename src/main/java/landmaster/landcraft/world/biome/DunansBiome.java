package landmaster.landcraft.world.biome;

import net.minecraft.util.math.*;
import net.minecraftforge.fml.relauncher.*;

public class DunansBiome extends LandiaBiome {
	public DunansBiome(BiomeProperties properties) {
		super(properties);
		this.theBiomeDecorator.treesPerChunk = 6;
		this.theBiomeDecorator.flowersPerChunk = 14;
		this.theBiomeDecorator.mushroomsPerChunk = 8;
		this.theBiomeDecorator.bigMushroomsPerChunk = 4;
		this.theBiomeDecorator.waterlilyPerChunk = 4;
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
