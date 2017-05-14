package landmaster.landcraft.world;

import landmaster.landcraft.config.*;
import mcjty.lib.compat.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.storage.*;
import net.minecraftforge.common.*;

public class LandiaWorldProvider extends CompatWorldProvider {
	public static DimensionType DIMENSION_TYPE;
	
	public static void register() {
		DIMENSION_TYPE = DimensionType.register("landia", "landia", Config.landiaDimensionID, LandiaWorldProvider.class, false);
		
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
	protected void initialize() {
		this.biomeProvider = new LandiaBiomeGenerator(getWorld());
		if (this.getWorld().getWorldInfo().getTerrainType() == WorldType.FLAT) {
			this.getWorld().getWorldInfo().setTerrainType(WorldType.DEFAULT);
		}
	}
	
	@Override
    public void calculateInitialWeather() {
        getWorld().thunderingStrength = 1.0F;
        getWorld().rainingStrength = 1.0F;
        getWorld().getWorldInfo().setThundering(true);
        getWorld().getWorldInfo().setRaining(true);
    }

    @Override
    public void updateWeather() {
        WorldInfo worldInfo = getWorld().getWorldInfo();
        if (!getWorld().isRemote) {
            getWorld().thunderingStrength = 1.0f;
            getWorld().rainingStrength = 1.0F;
            worldInfo.setThundering(true);
            worldInfo.setRaining(true);
        }
        worldInfo.setCleanWeatherTime(0);
        worldInfo.setThunderTime(worldInfo.getThunderTime() - 100);
        getWorld().updateWeatherBody();
    }
	
	@Override
    public IChunkGenerator createChunkGenerator() {
		return new LandiaChunkGenerator(getWorld());
	}
}
