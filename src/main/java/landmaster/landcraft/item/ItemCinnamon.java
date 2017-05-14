package landmaster.landcraft.item;

import landmaster.landcraft.content.*;
import mcjty.lib.compat.*;

public class ItemCinnamon extends CompatItemFood {
	public ItemCinnamon() {
		super(2, 0.5f, false);
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("cinnamon").setRegistryName("cinnamon");
	}
}
