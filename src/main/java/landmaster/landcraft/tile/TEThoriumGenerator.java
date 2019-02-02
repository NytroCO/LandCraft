package landmaster.landcraft.tile;

import java.util.*;
import java.util.stream.*;

import org.apache.commons.lang3.*;

import landmaster.landcraft.net.*;
import landmaster.landcraft.net.teupdate.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.energy.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.*;
import net.minecraftforge.items.*;
import net.minecraftforge.oredict.*;

public class TEThoriumGenerator extends TEEnergy
implements ITickable, RedstoneControl.Provider<TEThoriumGenerator>, IInventory {
	private ItemStackHandler ish;
	private FluidTank ft;
	
	private int progress = -1;
	
	public static final int THORIUM_BURN_TIME = 400;
	private static final int ENERGY_PER_TICK = 1000;
	private static final int WATER_PER_TICK = 10;
	
	static {
		PacketHandler.registerTEHandler(TEThoriumGenerator.class,
				new Handle<>(UpdateTEThoriumGenerator::new, UpdateTEThoriumGenerator::onMessage));
	}
	
	public TEThoriumGenerator() {
		super(400000, ENERGY_PER_TICK, ENERGY_PER_TICK);
		ish = new ItemStackHandler(1);
		ft = new FluidTank(8*Fluid.BUCKET_VOLUME) {
			@Override
			public boolean canFillFluidType(FluidStack fluid) {
				return fluid == null || fluid.getFluid() == FluidRegistry.WATER;
			}
			@Override
			public boolean canDrainFluidType(FluidStack fluid) {
				return fluid == null || fluid.getFluid() == FluidRegistry.WATER;
			}
			@Override
			protected void onContentsChanged() {
				TEThoriumGenerator.this.markDirty();
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
		return tag;
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

	@Override
	public void update() {
		if (getWorld().isRemote) return;
		if (this.isEnabled(this)) {
			if (progress < 0) {
				ItemStack is = ish.extractItem(0, 1, true);
				if (!is.isEmpty() && ArrayUtils.contains(OreDictionary.getOreIDs(is), OreDictionary.getOreID("ingotThorium"))) {
					ish.extractItem(0, 1, false);
					++progress;
					markDirty();
				}
			}
			if (progress >= 0) {
				FluidStack fs = ft.drain(new FluidStack(FluidRegistry.WATER, WATER_PER_TICK),
						false);
				if (this.receiveEnergy(null, ENERGY_PER_TICK, true) >= ENERGY_PER_TICK
						&& fs != null && fs.amount >= WATER_PER_TICK) {
					ft.drain(new FluidStack(FluidRegistry.WATER, WATER_PER_TICK), true);
					this.receiveEnergy(null, ENERGY_PER_TICK, false);
					++progress;
					markDirty();
				}
			}
			if (progress >= THORIUM_BURN_TIME) {
				progress = -1;
				markDirty();
			}
		}
		
		EnumMap<EnumFacing, IEnergyStorage> outputs = new EnumMap<>(EnumFacing.class);
		for (final EnumFacing facing: EnumFacing.VALUES) {
			final TileEntity te = world.getTileEntity(pos.offset(facing));
			if (te != null
					&& te.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())) {
				final IEnergyStorage storage = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
				if (storage.canReceive()) {
					outputs.put(facing, storage);
				}
			}
		}
		outputs.forEach((facing, storage) -> {
			int energyToSend = storage.receiveEnergy(ENERGY_PER_TICK / outputs.size(), true);
			int obtainedEnergy = this.extractEnergy(facing, energyToSend, false);
			storage.receiveEnergy(obtainedEnergy, false);
		});
	}
	
	public FluidStack getFluid() {
		return ft.getFluid();
	}
	public void setFluid(FluidStack fs) {
		ft.setFluid(fs);
	}
	
	public int getProgress() { return progress; }
	public void setProgress(int progress) { this.progress = progress; }

	@Override
	public RedstoneControl.State getRedstoneState() {
		return RedstoneControl.State.CONTINUOUS;
	}
	
	@Override
	public String getName() {
		return "thorium_generator";
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
	public boolean isUsableByPlayer(EntityPlayer player) {
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
			ish.setStackInSlot(i, ItemStack.EMPTY);
		}
	}
	@Override
	public boolean isEmpty() {
		return IntStream.range(0, ish.getSlots())
				.mapToObj(ish::getStackInSlot)
				.allMatch(ItemStack::isEmpty);
	}
}
