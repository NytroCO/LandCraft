package landmaster.landcraft.api;

import java.util.*;

import gnu.trove.map.*;
import gnu.trove.map.hash.*;
import net.minecraft.item.*;
import net.minecraftforge.oredict.*;

public class BreederFeedstock {
	private static final TIntIntMap feedstockMassDict = new TIntIntHashMap(),
			feedstockTempDict = new TIntIntHashMap();
	
	private static final List<OreMassTempTri> triList = new ArrayList<>();
	
	public static class OreMassTempTri {
		public final int oid;
		public final int mass, temp;
		
		public OreMassTempTri(int oid, int mass, int temp) {
			this.oid = oid;
			this.mass = mass;
			this.temp = temp;
		}
	}
	
	public static List<OreMassTempTri> getOreMassTempTris() {
		return Collections.unmodifiableList(triList);
	}
	
	public static void addOreDict(String ore, int mass, int temp) {
		addOreDict(ore, mass, temp, false);
	}
	
	private static void addOreDict(String ore, int mass, int temp, boolean force) {
		if (force || OreDictionary.doesOreNameExist(ore)) {
			addOreDict(OreDictionary.getOreID(ore), mass, temp);
		} else {
			LCLog.log.warn("OreDictionary entry "+ore+" does not exist; skipping for BreederFeedstock");
		}
	}
	
	private static void addOreDict(int oreId, int mass, int temp) {
		if (mass <= 0) {
			throw new IllegalArgumentException("Mass must be positive");
		} else if (temp <= 0) {
			throw new IllegalArgumentException("Temperature must be positive");
		}
		feedstockMassDict.put(oreId, mass);
		feedstockTempDict.put(oreId, temp);
		triList.add(new OreMassTempTri(oreId, mass, temp));
	}
	
	public static int getMass(ItemStack is) {
		if (!is.isEmpty()) {
			int[] arr = OreDictionary.getOreIDs(is);
			for (int i1 : arr) {
				if (feedstockMassDict.containsKey(i1)) {
					return feedstockMassDict.get(i1);
				}
			}
		}
		return 0;
	}
	
	public static int getTemp(ItemStack is) {
		if (!is.isEmpty()) {
			int[] arr = OreDictionary.getOreIDs(is);
			for (int i1 : arr) {
				if (feedstockTempDict.containsKey(i1)) {
					return feedstockTempDict.get(i1);
				}
			}
		}
		return 0;
	}
}
