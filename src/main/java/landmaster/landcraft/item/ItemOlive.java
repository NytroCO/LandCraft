package landmaster.landcraft.item;

import landmaster.landcraft.content.*;
import mcjty.lib.compat.*;

public class ItemOlive extends CompatItemFood {
	public ItemOlive() {
		super(3, 1.0f, false);
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("olive").setRegistryName("olive");
	}
}
