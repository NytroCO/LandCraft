package landmaster.landcraft.world.biome;

import net.minecraftforge.fml.relauncher.*;

public class TunisBiome extends LandiaBiome {

	public TunisBiome(BiomeProperties properties) {
		super(properties);
		this.theBiomeDecorator.treesPerChunk = 1;
		this.theBiomeDecorator.flowersPerChunk = 10;
		this.theBiomeDecorator.sandPerChunk = 2;
		this.theBiomeDecorator.sandPerChunk2 = 1;
		this.theBiomeDecorator.clayPerChunk = 3;
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getSkyColorByTemp(float currentTemperature) {
		return 0x003333;
	}
}
