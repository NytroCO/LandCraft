package landmaster.landcraft.item;

import landmaster.landcraft.content.*;
import net.minecraft.item.*;

public class ItemLandiaCrabFlesh extends ItemFood {
	public ItemLandiaCrabFlesh() {
		super(5, 1.0f, false);
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("landia_crab_flesh").setRegistryName("landia_crab_flesh");
	}
}
