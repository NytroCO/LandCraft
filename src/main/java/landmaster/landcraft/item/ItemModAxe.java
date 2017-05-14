package landmaster.landcraft.item;

import landmaster.landcraft.content.*;
import net.minecraft.item.*;

public class ItemModAxe extends ItemAxe {
	public ItemModAxe(ToolMaterial material, float damage, float speed, String name) {
		super(material, damage, speed);
		setUnlocalizedName(name).setRegistryName(name);
		setCreativeTab(LandCraftContent.creativeTab);
	}
}
