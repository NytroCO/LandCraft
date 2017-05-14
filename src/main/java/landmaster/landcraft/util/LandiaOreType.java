package landmaster.landcraft.util;

import java.util.*;

import net.minecraft.util.*;

public enum LandiaOreType implements IStringSerializable {
	KELLINE(5, 7, 7.5f, 13, -2.9f, 2500, 9.0f, 18),
	GARFAX(11, 4, 4.5f, 9.5f, -3.0f, 1300, 7.3f, 11),
	MORGANINE(9, 5, 5, 10.5f, -3.0f, 1600, 8.6f, 25),
	RACHELINE(7, 6, 6, 12, -3.0f, 2000, 8.3f, 22),
	FRISCION(13, 3, 4, 9.5f, -3.1f, 1100, 7.0f, 13);
	
	private int defaultChunks;
	private final int miningLevel;
	private final float damage, axeDamage, axeSpeed;
	private final int durability;
	private final float efficiency;
	private final int enchantability;
	
	LandiaOreType(int defaultChunks, int miningLevel, float damage,
			float axeDamage, float axeSpeed, int durability,
			float efficiency, int enchantability) {
		this.defaultChunks = defaultChunks;
		this.miningLevel = miningLevel;
		this.damage = damage;
		this.axeDamage = axeDamage;
		this.axeSpeed = axeSpeed;
		this.durability = durability;
		this.efficiency = efficiency;
		this.enchantability = enchantability;
	}
	
	public int numPerChunk() { return defaultChunks; }
	public void setNumPerChunk(int val) { defaultChunks = val; }
	
	public int getLevel() { return miningLevel; }
	public float getDamage() { return damage; }
	public float getAxeDamage() { return axeDamage; }
	public float getAxeSpeed() { return axeSpeed; }
	public int getDurability() { return durability; }
	public float getEfficiency() { return efficiency; }
	public int getEnchantability() { return enchantability; }
	
	@Override
	public String getName() {
		return name().toLowerCase(Locale.US);
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
