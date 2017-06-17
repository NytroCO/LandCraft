package landmaster.landcraft.world;

import landmaster.landcraft.config.*;
import landmaster.landcraft.world.biome.gen.*;
import landmaster.landcraft.world.save.*;
import net.minecraft.world.*;
import net.minecraft.world.gen.*;
import net.minecraft.world.storage.*;
import net.minecraftforge.common.*;

public class LandiaWorldProvider extends WorldProvider {
	public static DimensionType DIMENSION_TYPE;
	
	public static void register() {
		DIMENSION_TYPE = DimensionType.register("landia", "landia", Config.landiaDimensionID, LandiaWorldProvider.class,
				false);
		
		DimensionManager.registerDimension(Config.landiaDimensionID, DIMENSION_TYPE);
	}
	
	@Override
	public DimensionType getDimensionType() {
		return DIMENSION_TYPE;
	}
	
	@Override
	public String getSaveFolder() {
		return "LANDIA";
	}
	
	@Override
	protected void init() {
		super.init();
		this.biomeProvider = new LandiaBiomeGenerator(world);
		if (this.world.getWorldInfo().getTerrainType() == WorldType.FLAT) {
			this.world.getWorldInfo().setTerrainType(WorldType.DEFAULT);
		}
	}
	
	@Override
	public void calculateInitialWeather() {
		if (!world.isRemote) {
			if (LandiaWeather.get(world).isClear()) {
				world.thunderingStrength = 0.0F;
				world.rainingStrength = 0.0F;
				world.getWorldInfo().setThundering(false);
				world.getWorldInfo().setRaining(false);
			} else {
				world.thunderingStrength = 1.0F;
				world.rainingStrength = 1.0F;
				world.getWorldInfo().setThundering(true);
				world.getWorldInfo().setRaining(true);
			}
		}
	}
	
	@Override
	public void updateWeather() {
		if (LandiaWeather.get(world).isClear()) {
			WorldInfo worldInfo = world.getWorldInfo();
			if (!world.isRemote) {
				world.thunderingStrength = 0.0f;
				world.rainingStrength = 0.0F;
				worldInfo.setThundering(false);
				worldInfo.setRaining(false);
			}
			worldInfo.setCleanWeatherTime(0);
			worldInfo.setThunderTime(1000);
			worldInfo.setRainTime(1000);
		} else {
			WorldInfo worldInfo = world.getWorldInfo();
			if (!world.isRemote) {
				world.thunderingStrength = 1.0f;
				world.rainingStrength = 1.0F;
				worldInfo.setThundering(true);
				worldInfo.setRaining(true);
			}
			worldInfo.setCleanWeatherTime(0);
			worldInfo.setThunderTime(worldInfo.getThunderTime() - 100);
		}
		world.updateWeatherBody();
	}
	
	@Override
	public IChunkGenerator createChunkGenerator() {
		return new LandiaChunkGenerator(world);
	}
}
