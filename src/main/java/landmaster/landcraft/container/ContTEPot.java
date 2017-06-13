package landmaster.landcraft.container;

import landmaster.landcore.api.*;
import landmaster.landcraft.container.slots.*;
import landmaster.landcraft.net.*;
import landmaster.landcraft.net.teupdate.UpdateTEPot;
import landmaster.landcraft.tile.*;
import mcjty.lib.compat.*;
import mcjty.lib.tools.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.items.*;

public class ContTEPot extends ContEnergy {
	private int energy = 0, progress = 0, time = 0;
	private FluidStack fs;
	private TEPot te;
	
	public ContTEPot(EntityPlayer player, TEPot te) {
		super(player, te);
		this.te = te;
		addOwnSlots();
		addPlayerSlots(player.inventory);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		FluidStack nfs = te.getFluid();
		if (progress != te.getProgress() || time != te.getCachedTime()
				|| (fs != null && !fs.isFluidStackIdentical(nfs)) || (fs == null && nfs != null)
				|| energy != te.getEnergyStored(null)) {
			if (this.player instanceof EntityPlayerMP) {
				progress = te.getProgress();
				time = te.getCachedTime();
				fs = nfs;
				energy = te.getEnergyStored(null);
				if (fs != null) fs = fs.copy();
				PacketHandler.INSTANCE.sendTo(new PacketUpdateTE(new Coord4D(te), new UpdateTEPot(energy, progress, time)),
						(EntityPlayerMP)player);
			}
		}
	}
	
	private void addOwnSlots() {
		this.addSlotToContainer(new SlotItemHandlerBase(te, TEPot.Slots.IN1.ordinal(), 10, 20));
		this.addSlotToContainer(new SlotItemHandlerBase(te, TEPot.Slots.IN2.ordinal(), 30, 20));
		this.addSlotToContainer(new SlotItemHandlerBase(te, TEPot.Slots.IN3.ordinal(), 50, 20));
		this.addSlotToContainer(new SlotOutput(te, 30, 50));
	}
	private void addPlayerSlots(IInventory playerInv) {
		int slotY;
		for (slotY = 0; slotY < 3; slotY++) {
			for (int slotX = 0; slotX < 9; slotX++) {
				addSlotToContainer(new CompatSlot(playerInv, slotX + slotY * 9 + 9, 8 + slotX * 18, 84 + slotY * 18));
			}
		}
		for (slotY = 0; slotY < 9; slotY++) {
			addSlotToContainer(new CompatSlot(playerInv, slotY, 8 + slotY * 18, 142));
		}
    }
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		int invSize = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getSlots();
        ItemStack itemstack = ItemStackTools.getEmptyStack();
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < invSize) {
                if (!this.mergeItemStack(itemstack1, invSize, this.inventorySlots.size(), true)) {
                    return ItemStackTools.getEmptyStack();
                }
            } else if (!this.mergeItemStack(itemstack1, 0, invSize, false)) {
                return ItemStackTools.getEmptyStack();
            }
            if (ItemStackTools.getStackSize(itemstack1) == 0) {
                slot.putStack(ItemStackTools.getEmptyStack());
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }
}
