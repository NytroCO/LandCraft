package landmaster.landcraft.item;

import landmaster.landcraft.content.*;
import net.minecraft.item.*;

public class ItemModPickaxe extends ItemPickaxe {
	public ItemModPickaxe(ToolMaterial material, String name) {
		super(material);
		setUnlocalizedName(name).setRegistryName(name);
		setCreativeTab(LandCraftContent.creativeTab);
	}
}
