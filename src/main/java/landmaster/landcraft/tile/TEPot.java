package landmaster.landcraft.tile;

import javax.annotation.*;

import org.apache.commons.lang3.tuple.*;

import landmaster.landcraft.api.*;
import landmaster.landcraft.net.*;
import landmaster.landcraft.util.*;
import mcjty.lib.compat.*;
import mcjty.lib.tools.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraftforge.items.*;

public class TEPot extends TEEnergy
implements ITickable, RedstoneControl.Provider<TEPot>, CompatInventory {
	private ItemStackHandler ish;
	private FluidTank ft;
	
	private int progress = 0;
	
	private @Nonnull Pair<PotRecipes.RecipeProcess, PotRecipes.RecipeOutput> cache = PotRecipes.emptyRPair();
	
	public static enum Slots {
		IN1, IN2, IN3, OUT
	}
	
	static {
		PacketHandler.registerTEHandler(TEPot.class,
				new PacketHandler.Handle<>(PacketUpdateTEPot::new, PacketUpdateTEPot::onMessage));
	}
	
	public TEPot() {
		super(240000, 2000, 2000);
		ish = new ItemStackHandler(Slots.values().length);
		ft = new FluidTank(8*Fluid.BUCKET_VOLUME) {
			@Override
			protected void onContentsChanged() {
				TEPot.this.markDirty();
			}
		};
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		ish.deserializeNBT(tag.getCompoundTag("Inventory"));
		ft.readFromNBT(tag.getCompoundTag("Fluids"));
		progress = tag.getInteger("Progress");
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setTag("Inventory", ish.serializeNBT());
		tag.setTag("Fluids", ft.writeToNBT(new NBTTagCompound()));
		tag.setInteger("Progress", progress);
		// recipe info is not saved here; it is recalculated
		return tag;
	}
	
	@Override
	public void update() {
		if (world.isRemote) return; // server logic only
		
		ItemStack in1 = ItemStackTools.safeCopy(ish.getStackInSlot(Slots.IN1.ordinal()));
		ItemStack in2 = ItemStackTools.safeCopy(ish.getStackInSlot(Slots.IN2.ordinal()));
		ItemStack in3 = ItemStackTools.safeCopy(ish.getStackInSlot(Slots.IN3.ordinal()));
		FluidStack fs = ft.getFluid() != null ? ft.getFluid().copy() : null;
		if (PotRecipes.isEmptyRPair(cache)) {
			cache = PotRecipes.findRecipe(in1, in2, in3, fs); // start new recipe, or re-calculate from save
		} else if (PotRecipes.isEmptyROutput(cache.getLeft().process(in1, in2, in3, fs))) {
			cache = PotRecipes.findRecipe(in1, in2, in3, fs); // recipe changed
			progress = 0;
		}
		if (!PotRecipes.isEmptyRPair(cache)) {
			if (progress >= cache.getRight().time) {
				ItemStack toOut = ItemStackTools.safeCopy(cache.getRight().out);
				if (ItemStackTools.isEmpty(ish.insertItem(Slots.OUT.ordinal(), toOut, true))
						&& ItemStackTools.getStackSize(ish.extractItem(Slots.IN1.ordinal(), 1, true)) > 0
						&& ItemStackTools.getStackSize(ish.extractItem(Slots.IN2.ordinal(), 1, true)) > 0
						&& ItemStackTools.getStackSize(ish.extractItem(Slots.IN3.ordinal(), 1, true)) > 0) {
					ish.insertItem(Slots.OUT.ordinal(), toOut, false);
					ish.extractItem(Slots.IN1.ordinal(), 1, false);
					ish.extractItem(Slots.IN2.ordinal(), 1, false);
					ish.extractItem(Slots.IN3.ordinal(), 1, false);
					progress = 0; // restart from beginning; keep cache in case of multiple items
				}
			} else { // check for fluids and energy to progress
				int energy = cache.getRight().energyPerTick;
				int fluid = cache.getRight().fluidPerTick;
				if (this.extractEnergy(null, energy, true) >= energy
						&& Utils.getFluidAmount(ft.drain(fluid, false)) >= fluid) {
					this.extractEnergy(null, energy, false);
					ft.drain(fluid, true);
					++progress;
				}
			}
		} else { // reset; no recipes
			progress = 0;
		}
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
				|| capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY
				|| super.hasCapability(capability, facing);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T)ish;
		} else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return (T)ft;
		}
		return super.getCapability(capability, facing);
	}
	
	public FluidStack getFluid() {
		return ft.getFluid();
	}
	public void setFluid(FluidStack fs) {
		ft.setFluid(fs);
	}
	
	public int getProgress() { return progress; }
	public void setProgress(int progress) { this.progress = progress; }
	
	public int getCachedTime() {
		return cache.getRight().time;
	}
	
	@SideOnly(Side.CLIENT)
	private int clientTime;
	
	@SideOnly(Side.CLIENT)
	public int getClientTime() {
		return clientTime;
	}
	
	@SideOnly(Side.CLIENT)
	public void setClientTime(int time) {
		clientTime = time;
	}
	

	@Override
	public String getName() {
		return "pot";
	}
	@Override
	public boolean hasCustomName() {
		return true;
	}
	@Override
	public int getSizeInventory() {
		return ish.getSlots();
	}
	@Override
	public ItemStack getStackInSlot(int index) {
		return ish.getStackInSlot(index);
	}
	@Override
	public ItemStack decrStackSize(int index, int count) {
		return ish.extractItem(index, count, false);
	}
	@Override
	public ItemStack removeStackFromSlot(int index) {
		return ish.extractItem(index, Integer.MAX_VALUE, false);
	}
	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		ish.setStackInSlot(index, stack);
	}
	@Override
	public int getInventoryStackLimit() {
		return 64;
	}
	@Override
	public boolean isUsable(EntityPlayer player) {
		return true;
	}
	@Override
	public void openInventory(EntityPlayer player) {
	}
	@Override
	public void closeInventory(EntityPlayer player) {
	}
	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		return true;
	}
	@Override
	public int getField(int id) {
		return 0;
	}
	@Override
	public void setField(int id, int value) {
	}
	@Override
	public int getFieldCount() {
		return 0;
	}
	@Override
	public void clear() {
		for (int i=0; i<ish.getSlots(); ++i) {
			ish.setStackInSlot(i, ItemStackTools.getEmptyStack());
		}
	}

	@Override
	public RedstoneControl.State getRedstoneState() {
		return RedstoneControl.State.CONTINUOUS;
	}
}
