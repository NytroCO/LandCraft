package landmaster.landcraft.item;

import landmaster.landcraft.content.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

public class ItemPho extends ItemFood {
	public ItemPho() {
		super(10, 0.7f, false); // base for soup only; overriden if noodles present (meta > 0)
		this.setHasSubtypes(true);
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("pho").setRegistryName("pho");
		this.setPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 4), 1);
	}
	
	public static boolean hasNoodles(ItemStack stack) { return stack.getMetadata() > 0; }
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return hasNoodles(stack) ? 16 : 8;
	}
	
	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
		super.onFoodEaten(stack, worldIn, player);
		if (!worldIn.isRemote) {
			if (hasNoodles(stack)) {
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 4));
				player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 2000, 0));
				player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 400, 1));
				player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 3));
			} else {
				player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 0));
				player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 1));
			}
		}
	}
	
	@Override
	public int getHealAmount(ItemStack stack) {
		if (hasNoodles(stack)) {
			return 15;
		}
		return super.getHealAmount(stack);
	}
	
	@Override
	public float getSaturationModifier(ItemStack stack) {
		if (hasNoodles(stack)) {
			return 1.0f;
		}
		return super.getSaturationModifier(stack);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		String baseName = super.getUnlocalizedName(stack);
		if (hasNoodles(stack)) {
			baseName += ".full";
		}
		return baseName;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)) {
			subItems.add(new ItemStack(this, 1, 0)); // broth only
			subItems.add(new ItemStack(this, 1, 1)); // with noodles
		}
	}
}
