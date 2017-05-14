package landmaster.landcraft.item;

import landmaster.landcraft.content.*;
import net.minecraft.init.*;
import net.minecraft.item.*;

public class ItemRice extends ItemSeedFood {
	public ItemRice() {
		super(2, 0.5f, LandCraftContent.rice_crop, Blocks.FARMLAND);
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("rice").setRegistryName("rice");
	}
}
