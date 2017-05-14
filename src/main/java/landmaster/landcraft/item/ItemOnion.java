package landmaster.landcraft.item;

import landmaster.landcraft.content.*;
import net.minecraft.init.*;
import net.minecraft.item.*;

public class ItemOnion extends ItemSeedFood {
	public ItemOnion() {
		super(2, 0.5f, LandCraftContent.onion_crop, Blocks.FARMLAND);
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("onion").setRegistryName("onion");
	}
}
