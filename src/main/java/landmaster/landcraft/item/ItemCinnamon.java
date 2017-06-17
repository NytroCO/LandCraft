package landmaster.landcraft.item;

import landmaster.landcraft.content.*;
import net.minecraft.item.*;

public class ItemCinnamon extends ItemFood {
	public ItemCinnamon() {
		super(2, 0.5f, false);
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("cinnamon").setRegistryName("cinnamon");
	}
}
