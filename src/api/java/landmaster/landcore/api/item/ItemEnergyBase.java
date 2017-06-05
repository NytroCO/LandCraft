package landmaster.landcore.api.item;

import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.energy.*;
import net.minecraftforge.energy.IEnergyStorage;

public class ItemEnergyBase extends CompatItemEnergyContainer implements IEnergySetter {
	private class Energy implements IEnergyStorage {
		ItemStack is;
		public Energy(ItemStack is) {
			this.is = is;
		}
		
		@Override
		public int receiveEnergy(int maxReceive, boolean simulate) {
			return ItemEnergyBase.this.receiveEnergy(is, maxReceive, simulate);
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate) {
			return ItemEnergyBase.this.extractEnergy(is, maxExtract, simulate);
		}

		@Override
		public int getEnergyStored() {
			return ItemEnergyBase.this.getEnergyStored(is);
		}

		@Override
		public int getMaxEnergyStored() {
			return ItemEnergyBase.this.getMaxEnergyStored(is);
		}

		@Override
		public boolean canExtract() {
			return true;
		}

		@Override
		public boolean canReceive() {
			return true;
		}
	}
	
	private class Provider implements ICapabilityProvider {
		Energy energy;
		
		public Provider(ItemStack is) {
			energy = new Energy(is);
		}
		
		@Override
		public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
			return capability == CapabilityEnergy.ENERGY;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
			if (capability == CapabilityEnergy.ENERGY) {
				return (T)energy;
			}
			return null;
		}
	}
	
	public ItemEnergyBase(int capacity, int maxReceive, int maxExtract) {
		super(capacity, maxReceive, maxExtract);
	}
	
	@Override
	public ICapabilityProvider initCapabilities(ItemStack is, NBTTagCompound capNbt) {
		return new Provider(is);
	}

	@Override
	public void setEnergyStored(ItemStack is, int energy) {
		if (is.getTagCompound() == null) {
			is.setTagCompound(new NBTTagCompound());
		}
		is.getTagCompound().setInteger("Energy", energy);
	}
}
