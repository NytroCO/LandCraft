package landmaster.landcraft.util;

import java.util.*;

import net.minecraft.util.*;

public enum LandiaOreType implements IStringSerializable {
	KELLINE(7,5),
	GARFAX(4,11),
	MORGANINE(5,9),
	RACHELINE(6,7),
	FRISCION(3,13);
	
	private final int miningLevel;
	private int defaultChunks;
	
	LandiaOreType(int miningLevel, int defaultChunks) {
		this.miningLevel = miningLevel;
		this.defaultChunks = defaultChunks;
	}
	
	public int getLevel() { return miningLevel; }
	public int numPerChunk() { return defaultChunks; }
	public void setNumPerChunk(int val) { defaultChunks = val; }
	
	@Override
	public String getName() {
		return name().toLowerCase(Locale.US);
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
