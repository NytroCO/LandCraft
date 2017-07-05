package landmaster.landcraft.tile;

import cofh.redstoneflux.api.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.energy.*;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fml.common.*;

@Optional.InterfaceList({
	@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyReceiver", modid = "redstoneflux"),
	@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyProvider", modid = "redstoneflux")
})
public class TEEnergy extends TileEntity implements IEnergyReceiver, IEnergyProvider {
	public static class Storage extends EnergyStorage {
		public Storage(int capacity) {
			super(capacity);
		}
		public Storage(int capacity, int maxTransfer) {
			super(capacity, maxTransfer);
		}
		public Storage(int capacity, int maxReceive, int maxExtract) {
			super(capacity, maxReceive, maxExtract);
		}
		public void setEnergyStored(int energy) {
			this.energy = energy;
		}
	}
	
	Storage es;
	
	public TEEnergy(int capacity, int maxReceive, int maxExtract) {
		es = new Storage(capacity, maxReceive, maxExtract);
	}
	
	public void setEnergyStored(EnumFacing facing, int energy) {
		es.setEnergyStored(energy);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		es.setEnergyStored(tag.getInteger("Energy"));
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setInteger("Energy", es.getEnergyStored());
		return tag;
	}
	
	@Override
	public int getEnergyStored(EnumFacing from) {
		return es.getEnergyStored();
	}
	
	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return es.getMaxEnergyStored();
	}
	
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}
	
	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return es.extractEnergy(maxExtract, simulate);
	}
	
	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return es.receiveEnergy(maxReceive, simulate);
	}
	
	@Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability (Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY) {
			return (T)es;
		}
		return super.getCapability(capability, facing);
	}
	
	@Override
    public boolean hasCapability (Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityEnergy.ENERGY
				|| super.hasCapability(capability, facing);
	}
}
