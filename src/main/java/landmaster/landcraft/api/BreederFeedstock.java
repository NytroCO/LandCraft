package landmaster.landcraft.api;

import java.util.*;

import net.minecraft.item.*;
import net.minecraftforge.oredict.*;

public class BreederFeedstock {
	private static final Map<Integer, Integer> feedstockMassDict = new HashMap<>(),
			feedstockTempDict = new HashMap<>();
	
	public static void addOreDict(String ore, int mass, int temp) {
		if (OreDictionary.doesOreNameExist(ore)) {
			addOreDict(OreDictionary.getOreID(ore), mass, temp);
		}
	}
	
	public static void addOreDict(int oreId, int mass, int temp) {
		if (mass <= 0) {
			throw new IllegalArgumentException("Mass must be positive");
		} else if (temp <= 0) {
			throw new IllegalArgumentException("Temperature must be positive");
		}
		feedstockMassDict.put(oreId, mass);
		feedstockTempDict.put(oreId, temp);
	}
	
	public static int getMass(ItemStack is) {
		int[] arr = OreDictionary.getOreIDs(is);
		for (int i=0; i<arr.length; ++i) {
			if (feedstockMassDict.containsKey(arr[i])) {
				return feedstockMassDict.get(arr[i]);
			}
		}
		return 0;
	}
	
	public static int getTemp(ItemStack is) {
		int[] arr = OreDictionary.getOreIDs(is);
		for (int i=0; i<arr.length; ++i) {
			if (feedstockTempDict.containsKey(arr[i])) {
				return feedstockTempDict.get(arr[i]);
			}
		}
		return 0;
	}
}
