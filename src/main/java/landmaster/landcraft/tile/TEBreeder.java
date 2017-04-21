package landmaster.landcraft.tile;

import org.apache.commons.lang3.*;

import landmaster.landcraft.api.*;
import li.cil.oc.api.machine.*;
import li.cil.oc.api.network.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.items.*;
import net.minecraftforge.oredict.*;

@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers")
public class TEBreeder extends TileEntity
implements ITickable, SimpleComponent, RedstoneControl.Provider<TEBreeder> {
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
	
	public double maxTemp() {
		return 50000;
	}
	
	@Override
	public void update() {
		if (worldObj.isRemote || !isEnabled(this)) return;
		ItemStack reactant = ItemStack.copyItemStack(ish.getStackInSlot(Slots.REACTANT.ordinal()));
		if (reactant != null && ArrayUtils.contains(OreDictionary.getOreIDs(reactant), OreDictionary.getOreID("ingotThorium"))) {
			int consume = Math.min(reactant.stackSize, (MAX_FUEL - fuel)/THORIUM_SCALAR);
			fuel += consume * THORIUM_SCALAR;
			ish.extractItem(Slots.REACTANT.ordinal(), consume, false);
			markDirty();
		}
		if (fuel > 0) {
			int fuelConsumption = 8;
			fuel -= fuelConsumption;
			temperature += getTempFromFuel(fuelConsumption);
			markDirty();
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
					markDirty();
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
			markDirty();
		}
		
		if (temperature > 0) {
			temperature = Math.max(temperature-5, 0);
			markDirty();
		}
		
		if (temperature > maxTemp()) {
			worldObj.newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 3, true, true);
			worldObj.destroyBlock(pos, true);
		}
	}
	
	public int getFuelConsumption() {
		return 8;
	}
	
	public double getTempFromFuel(int fuelConsumption) {
		return 20+700*Math.exp(-0.0001*Math.pow(temperature/200-100,2))*fuelConsumption/8.0;
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
	@Override
	public RedstoneControl.State getRedstoneState() {
		return RedstoneControl.State.CONTINUOUS;
	}
}
