package landmaster.landcraft.world.save;

import landmaster.landcraft.*;
import net.minecraft.nbt.*;
import net.minecraft.world.*;
import net.minecraft.world.storage.*;

public class LandiaWeather extends WorldSavedData {
	public static final String NAME = LandCraft.MODID + "_landia_weather";
	
	private boolean clear = false;
	
	public LandiaWeather() {
		this(NAME);
	}
	public LandiaWeather(String s) {
		super(s);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		clear = nbt.getBoolean("Clear");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setBoolean("Clear", clear);
		return compound;
	}
	
	public static LandiaWeather get(World world) {
		MapStorage storage = world.getPerWorldStorage();
		LandiaWeather data = (LandiaWeather)storage.getOrLoadData(LandiaWeather.class, NAME);
		if (data == null) {
			data = new LandiaWeather();
			storage.setData(NAME, data);
		}
		return data;
	}
	
	public boolean isClear() { return this.clear; }
	public void setClear(boolean clear) { this.clear = clear; }
	
}
