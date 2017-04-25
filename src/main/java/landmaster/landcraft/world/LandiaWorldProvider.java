package landmaster.landcraft.world;

import landmaster.landcraft.config.*;
import mcjty.lib.compat.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.*;
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
		this.biomeProvider = new BiomeProviderSingle(LandcraftBiomes.dunans);
	}
	
	@Override
    public IChunkGenerator createChunkGenerator() {
		return new LandiaChunkGenerator(getWorld());
	}
}
