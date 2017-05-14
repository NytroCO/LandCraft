package landmaster.landcraft.item;

import java.util.*;

import landmaster.landcraft.content.*;
import mcjty.lib.compat.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import net.minecraftforge.fml.relauncher.*;

public class ItemPotatoOnionPastry extends CompatItemFood {
	public ItemPotatoOnionPastry() {
		super(5, 0.5f, false); // base for raw; overriden for cooked (meta > 0)
		this.setHasSubtypes(true);
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("potato_onion_pastry").setRegistryName("potato_onion_pastry");
	}
	
	public static boolean isCooked(ItemStack stack) { return stack.getMetadata() > 0; }
	
	@Override
	public int getHealAmount(ItemStack stack) {
		if (isCooked(stack)) {
			return 9;
		}
		return super.getHealAmount(stack);
	}
	
	@Override
	public float getSaturationModifier(ItemStack stack) {
		if (isCooked(stack)) {
			return 0.8f;
		}
		return super.getSaturationModifier(stack);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		String baseName = super.getUnlocalizedName(stack);
		if (isCooked(stack)) {
			baseName += ".cooked";
		}
		return baseName;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	protected void clGetSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		subItems.add(new ItemStack(itemIn, 1, 0)); // raw
		subItems.add(new ItemStack(itemIn, 1, 1)); // cooked
	}
}
