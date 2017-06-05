package landmaster.landcraft.item;

import landmaster.landcore.api.item.*;
import landmaster.landcraft.content.*;
import mcjty.lib.tools.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.network.play.client.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.gameevent.*;

public class ItemLandmastersWings extends ItemEnergyBase {
	public static final int ENERGY_PER_TICK = 100;
	
	public ItemLandmastersWings() {
		super(3000000, 3000000, 3000000);
		this.maxStackSize = 1;
		this.setUnlocalizedName("landmasters_wings").setRegistryName("landmasters_wings");
		this.setCreativeTab(LandCraftContent.creativeTab);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Override
    public boolean showDurabilityBar(ItemStack itemStack) {
		return true;
	}
	
	@Override
    public double getDurabilityForDisplay(ItemStack stack) {
		return (double)(getMaxEnergyStored(stack) - getEnergyStored(stack)) / getMaxEnergyStored(stack);
	}
	
	@Override
	public ActionResult<ItemStack> clOnItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		
		if (worldIn.isRemote) return new ActionResult<>(EnumActionResult.PASS, stack);
		
		EntityEquipmentSlot slot = EntityLiving.getSlotForItemStack(stack);
		ItemStack old = playerIn.getItemStackFromSlot(slot);
		
		if (ItemStackTools.isEmpty(old)) { // vacant
			playerIn.setItemStackToSlot(slot, stack.copy());
			stack = ItemStackTools.setStackSize(stack, 0);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		
		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}
	
	@SubscribeEvent
	public void checkFly(TickEvent.PlayerTickEvent event) {
		if (event.player.getEntityWorld().isRemote
				|| !(event.player instanceof EntityPlayerMP)
				|| event.player.capabilities.isCreativeMode /* no effect on creative */) {
			return;
		}
		
		ItemStack stack = event.player.getItemStackFromSlot(
				EntityLiving.getSlotForItemStack(new ItemStack(this)));
		
		if (stack != null && stack.getItem() == this && this.extractEnergy(stack, ENERGY_PER_TICK, true) >= ENERGY_PER_TICK) {
			event.player.capabilities.allowFlying = true;
			if (event.player.capabilities.isFlying) {
				this.extractEnergy(stack, ENERGY_PER_TICK, false);
			}
		} else {
			event.player.capabilities.allowFlying = false;
			event.player.capabilities.isFlying = false;
		}
		((EntityPlayerMP)event.player).connection.processPlayerAbilities(new CPacketPlayerAbilities(event.player.capabilities));
	}
}
