package landmaster.landcraft.item;

import landmaster.landcraft.content.*;
import net.minecraft.item.*;

public class ItemOlive extends ItemFood {
	public ItemOlive() {
		super(3, 1.0f, false);
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("olive").setRegistryName("olive");
	}
}
