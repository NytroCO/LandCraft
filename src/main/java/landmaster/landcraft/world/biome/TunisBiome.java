package landmaster.landcraft.world.biome;

import net.minecraftforge.fml.relauncher.*;

class TunisBiome extends LandiaBiome {

	public TunisBiome(BiomeProperties properties) {
		super(properties);
		this.decorator.treesPerChunk = 1;
		this.decorator.flowersPerChunk = 10;
		this.decorator.sandPatchesPerChunk = 3;
		this.decorator.clayPerChunk = 3;
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getSkyColorByTemp(float currentTemperature) {
		return 0x003333;
	}
}
