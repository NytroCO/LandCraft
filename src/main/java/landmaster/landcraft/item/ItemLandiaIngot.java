package landmaster.landcraft.item;

import java.util.*;

import landmaster.landcraft.*;
import landmaster.landcraft.util.*;
import mcjty.lib.compat.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import net.minecraftforge.fml.relauncher.*;

public class ItemLandiaIngot extends CompatItem {
	public ItemLandiaIngot() {
		setHasSubtypes(true);
		setUnlocalizedName("landia_ingot").setRegistryName("landia_ingot");
		setCreativeTab(LandCraft.creativeTab);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return super.getUnlocalizedName(stack) + "." + LandiaOreType.values()[stack.getMetadata()];
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	protected void clGetSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		for (int i=0; i<LandiaOreType.values().length; ++i) {
			subItems.add(new ItemStack(itemIn, 1, i));
		}
	}
}
