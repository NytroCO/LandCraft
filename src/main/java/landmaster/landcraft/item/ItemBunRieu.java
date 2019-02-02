package landmaster.landcraft.item;

import landmaster.landcraft.content.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.potion.*;
import net.minecraft.util.NonNullList;
import net.minecraft.world.*;

public class ItemBunRieu extends ItemFood {
	public ItemBunRieu() {
		super(11, 0.6f, false); // base for soup only; overriden if noodles present (meta > 0)
		this.setHasSubtypes(true);
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("bun_rieu").setRegistryName("bun_rieu");
	}
	
	private static boolean hasNoodles(ItemStack stack) { return stack.getMetadata() > 0; }
	
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return hasNoodles(stack) ? 16 : 8;
	}
	
	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
		super.onFoodEaten(stack, worldIn, player);
		if (!worldIn.isRemote) {
			if (hasNoodles(stack)) {
				player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 2000, 4));
				player.addPotionEffect(new PotionEffect(MobEffects.LUCK, 2400, 1));
				player.addPotionEffect(new PotionEffect(MobEffects.HASTE, 2400, 1));
				player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 2000, 1));
			} else {
				player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 2000, 2));
				player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 2000, 0));
			}
		}
	}
	
	@Override
	public int getHealAmount(ItemStack stack) {
		if (hasNoodles(stack)) {
			return 16;
		}
		return super.getHealAmount(stack);
	}
	
	@Override
	public float getSaturationModifier(ItemStack stack) {
		if (hasNoodles(stack)) {
			return 0.9f;
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
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)) {
			subItems.add(new ItemStack(this, 1, 0)); // broth only
			subItems.add(new ItemStack(this, 1, 1)); // with noodles
		}
	}
}
