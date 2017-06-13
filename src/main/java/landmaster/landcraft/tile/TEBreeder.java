package landmaster.landcraft.tile;

import org.apache.commons.lang3.*;

import landmaster.landcraft.api.*;
import landmaster.landcraft.net.*;
import landmaster.landcraft.net.teupdate.UpdateTEBreeder;
import landmaster.landcraft.util.*;
import li.cil.oc.api.machine.*;
import li.cil.oc.api.network.*;
import mcjty.lib.compat.*;
import mcjty.lib.tools.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.items.*;
import net.minecraftforge.oredict.*;

@OptionalM(modids = {"OpenComputers", "opencomputers"}, stripped = "li/cil/oc/api/network/SimpleComponent")
public class TEBreeder extends TileEntity
implements ITickable, SimpleComponent, RedstoneControl.Provider<TEBreeder>, CompatInventory {
	private ItemStackHandler ish;
	private double temperature;
	private int fuel, product;
	public static final int MAX_FUEL = 8192;
	public static final int THORIUM_SCALAR = 32;
	
	public static enum Slots {
		FEEDSTOCK, REACTANT, OUTPUT
	}
	
	static {
		PacketHandler.registerTEHandler(TEBreeder.class,
				new Handle<>(UpdateTEBreeder::new, UpdateTEBreeder::onMessage));
	}
	
	public TEBreeder() {
		super();
		temperature = fuel = product = 0;
		ish = new ItemStackHandler(Slots.values().length);
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
		if (getWorld().isRemote || !isEnabled(this)) return;
		ItemStack reactant = ItemStackTools.safeCopy(ish.getStackInSlot(Slots.REACTANT.ordinal()));
		if (!ItemStackTools.isEmpty(reactant) && ArrayUtils.contains(OreDictionary.getOreIDs(reactant), OreDictionary.getOreID("ingotThorium"))) {
			int consume = Math.min(ItemStackTools.getStackSize(reactant), (MAX_FUEL - fuel)/THORIUM_SCALAR);
			fuel += consume * THORIUM_SCALAR;
			ish.extractItem(Slots.REACTANT.ordinal(), consume, false);
			markDirty();
		}
		if (fuel > 0) {
			int fuelConsumption = getFuelConsumption();
			fuel -= fuelConsumption;
			temperature += getTempFromFuel(fuelConsumption);
			markDirty();
		}
		ItemStack feedstock = ItemStackTools.safeCopy(ish.getStackInSlot(Slots.FEEDSTOCK.ordinal()));
		int mass, temp;
		if (!ItemStackTools.isEmpty(feedstock) && (mass = BreederFeedstock.getMass(feedstock)) > 0) {
			temp = BreederFeedstock.getTemp(feedstock);
			if (temp < temperature) {
				ItemStack feed = ish.extractItem(Slots.FEEDSTOCK.ordinal(), 1, false);
				if (ItemStackTools.getStackSize(feed) >= 1) {
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
			int remSize = ItemStackTools.getStackSize(rem);
			product = remSize * THORIUM_SCALAR + thoriumRem;
			markDirty();
		}
		
		if (temperature > 0) {
			temperature = Math.max(temperature-5, 0);
			markDirty();
		}
		
		if (temperature > maxTemp()) {
			getWorld().newExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 3, true, true);
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
    @OptionalM(modids = {"OpenComputers", "opencomputers"})
    public Object[] getFuel(Context context, Arguments args) {
		return new Object[] {fuel};
	}
	
	@Callback
	@OptionalM(modids = {"OpenComputers", "opencomputers"})
    public Object[] getTemp(Context context, Arguments args) {
		return new Object[] {temperature};
	}
	
	@Callback
	@OptionalM(modids = {"OpenComputers", "opencomputers"})
    public Object[] getProduct(Context context, Arguments args) {
		return new Object[] {product};
	}
	@Override
	public RedstoneControl.State getRedstoneState() {
		return RedstoneControl.State.CONTINUOUS;
	}
	@Override
	public String getName() {
		return getComponentName();
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
}
