package landmaster.landcraft.item;

import landmaster.landcraft.*;
import net.minecraft.item.*;

public class ItemModShovel extends ItemSpade {
	public ItemModShovel(ToolMaterial material, String name) {
		super(material);
		setUnlocalizedName(name).setRegistryName(name);
		setCreativeTab(LandCraft.creativeTab);
	}
}
