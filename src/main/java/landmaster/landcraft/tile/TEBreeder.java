package landmaster.landcraft.tile;

import org.apache.commons.lang3.*;

import landmaster.landcraft.api.*;
import li.cil.oc.api.machine.*;
import li.cil.oc.api.network.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.network.play.server.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.items.*;
import net.minecraftforge.oredict.*;

@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers")
public class TEBreeder extends TileEntity implements ITickable, SimpleComponent {
	private ItemStackHandler ish;
	private double temperature;
	private int fuel, product;
	public static final int MAX_FUEL = 8192;
	public static final int THORIUM_SCALAR = 32;
	
	public static enum Slots {
		FEEDSTOCK, REACTANT, OUTPUT
	}
	
	public TEBreeder() {
		super();
		temperature = fuel = product = 0;
		ish = new ItemStackHandler(3);
	}
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		ish.deserializeNBT(tag.getCompoundTag("Inventory"));
		fuel = tag.getInteger("Fuel");
		temperature = tag.getDouble("Temperature");
		product = tag.getInteger("Product");
	}
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		tag = super.writeToNBT(tag);
		tag.setTag("Inventory", ish.serializeNBT());
		tag.setInteger("Fuel", fuel);
		tag.setDouble("Temperature", temperature);
		tag.setInteger("Product", product);
		return tag;
	}
	@Override
	public void update() {
		if (worldObj.isRemote) return;
		ItemStack reactant = ItemStack.copyItemStack(ish.getStackInSlot(Slots.REACTANT.ordinal()));
		if (reactant != null && ArrayUtils.contains(OreDictionary.getOreIDs(reactant), OreDictionary.getOreID("ingotThorium"))) {
			int consume = Math.min(reactant.stackSize, (MAX_FUEL - fuel)/THORIUM_SCALAR);
			fuel += consume * THORIUM_SCALAR;
			ish.extractItem(Slots.REACTANT.ordinal(), consume, false);
		}
		if (fuel > 0) {
			int fuelConsumption = 8;
			fuel -= fuelConsumption;
			temperature += getTempFromFuel(fuelConsumption);
		}
		ItemStack feedstock = ItemStack.copyItemStack(ish.getStackInSlot(Slots.FEEDSTOCK.ordinal()));
		int mass, temp;
		if (feedstock != null && (mass = BreederFeedstock.getMass(feedstock)) > 0) {
			temp = BreederFeedstock.getTemp(feedstock);
			if (temp < temperature) {
				ItemStack feed = ish.extractItem(Slots.FEEDSTOCK.ordinal(), 1, false);
				if (feed != null && feed.stackSize >= 1) {
					temperature -= temp;
					product += mass;
				}
			}
		}
		int thoriumProduct = product / THORIUM_SCALAR;
		int thoriumRem = product % THORIUM_SCALAR;
		if (thoriumProduct > 0) {
			ItemStack rem = ish.insertItem(Slots.OUTPUT.ordinal(), new ItemStack(
					Item.REGISTRY.getObject(new ResourceLocation("landcore:item_ingot")), thoriumProduct, 0), false);
			int remSize = rem != null ? rem.stackSize : 0;
			product = remSize * THORIUM_SCALAR + thoriumRem;
		}
		temperature = Math.max(temperature-5, 0);
	}
	
	public double getTempFromFuel(int fuelConsumption) {
		return fuelConsumption;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
				|| super.hasCapability(capability, facing);
	}
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T)ish;
		}
		return super.getCapability(capability, facing);
	}
	
	// Getters
	public double getTemp() { return temperature; }
	public int getFuel() { return fuel; }
	public int getProduct() { return product; }
	
	// Setters
	public void setTemp(double arg) { temperature = arg; }
	public void setFuel(int arg) { fuel = arg; }
	public void setProduct(int arg) { product = arg; }
	
	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = super.getUpdateTag();
		return writeToNBT(tag);
	}
	
	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		super.handleUpdateTag(tag);
	}
	
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, getBlockMetadata(), getUpdateTag());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		if (net.getDirection() == EnumPacketDirection.CLIENTBOUND) {
			readFromNBT(pkt.getNbtCompound());
		}
	}
	
	@Override
	public String getComponentName() {
		return "breeder_reactor";
	}
	
	@Callback
    @Optional.Method(modid = "OpenComputers")
    public Object[] getFuel(Context context, Arguments args) {
		return new Object[] {fuel};
	}
	
	@Callback
    @Optional.Method(modid = "OpenComputers")
    public Object[] getTemp(Context context, Arguments args) {
		return new Object[] {temperature};
	}
	
	@Callback
    @Optional.Method(modid = "OpenComputers")
    public Object[] getProduct(Context context, Arguments args) {
		return new Object[] {product};
	}
}