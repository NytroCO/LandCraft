package landmaster.landcraft.item;

import landmaster.landcraft.content.*;
import net.minecraft.item.*;

public class ItemTomato extends ItemFood {
	public ItemTomato() {
		super(3, 1.0f, false);
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("tomato").setRegistryName("tomato");
	}
}
