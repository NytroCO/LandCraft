package landmaster.landcraft.item;

import landmaster.landcraft.content.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.*;

public class ItemPotatoOnionPastry extends ItemFood {
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
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)) {
			subItems.add(new ItemStack(this, 1, 0)); // raw
			subItems.add(new ItemStack(this, 1, 1)); // cooked
		}
	}
}
