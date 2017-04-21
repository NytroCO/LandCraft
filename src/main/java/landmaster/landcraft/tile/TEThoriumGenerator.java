package landmaster.landcraft.tile;

import org.apache.commons.lang3.*;

import li.cil.oc.api.machine.*;
import li.cil.oc.api.network.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.items.*;
import net.minecraftforge.oredict.*;

@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers")
public class TEThoriumGenerator extends TEEnergy
implements ITickable, SimpleComponent, RedstoneControl.Provider<TEThoriumGenerator> {
	private ItemStackHandler ish;
	private FluidTank ft;
	
	private int progress = -1;
	
	public static final int THORIUM_BURN_TIME = 400;
	public static final int ENERGY_PER_TICK = 1000;
	public static final int WATER_PER_TICK = 10;
	
	public TEThoriumGenerator() {
		super(240000, ENERGY_PER_TICK, ENERGY_PER_TICK);
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
		if (!this.isEnabled(this)) return;
		if (progress < 0) {
			ItemStack is = ish.extractItem(0, 1, true);
			if (is != null && ArrayUtils.contains(OreDictionary.getOreIDs(is), OreDictionary.getOreID("ingotThorium"))) {
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
	
	public FluidStack getFluid() {
		return ft.getFluid();
	}
	
	public int getProgress() { return progress; }
	public void setProgress(int progress) { this.progress = progress; }
	
	@Override
	public String getComponentName() {
		return "thorium_generator";
	}
	
	@Callback
    @Optional.Method(modid = "OpenComputers")
    public Object[] getPower(Context context, Arguments args) {
		return new Object[] { this.getEnergyStored(null) };
	}

	@Override
	public RedstoneControl.State getRedstoneState() {
		return RedstoneControl.State.CONTINUOUS;
	}
}
