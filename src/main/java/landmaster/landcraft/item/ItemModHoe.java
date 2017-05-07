package landmaster.landcraft.item;

import landmaster.landcraft.*;
import net.minecraft.item.*;

public class ItemModHoe extends ItemHoe {
	public ItemModHoe(ToolMaterial material, String name) {
		super(material);
		setUnlocalizedName(name).setRegistryName(name);
		setCreativeTab(LandCraft.creativeTab);
	}
}
