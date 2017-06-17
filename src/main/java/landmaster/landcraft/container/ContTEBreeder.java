package landmaster.landcraft.container;

import landmaster.landcore.api.*;
import landmaster.landcraft.container.slots.*;
import landmaster.landcraft.net.*;
import landmaster.landcraft.net.teupdate.*;
import landmaster.landcraft.tile.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraftforge.items.*;

public class ContTEBreeder extends Container {
	private final TEBreeder te;
	private final EntityPlayer player;
	
	private int fuel = 0, product = 0;
	private double temp = 0.0;
	
	public ContTEBreeder(EntityPlayer player, TEBreeder te) {
		this.te = te;
		this.player = player;
		addOwnSlots();
		addPlayerSlots(player.inventory);
	}
	public TEBreeder getTE() {
		return te;
	}
	public EntityPlayer getPlayer() {
		return player;
	}
	public IInventory getPlayerInv() {
		return player.inventory;
	}
	
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (temp != te.getTemp() || fuel != te.getFuel() || product != te.getProduct()) {
			if (player instanceof EntityPlayerMP) {
				PacketHandler.INSTANCE.sendTo(new PacketUpdateTE(new Coord4D(te), new UpdateTEBreeder(te)), (EntityPlayerMP)player);
			}
			temp = te.getTemp();
			fuel = te.getFuel();
			product = te.getProduct();
		}
	}
	
	
	private void addOwnSlots() {
		addSlotToContainer(new SlotFeedstock(te, 16, 30));
		addSlotToContainer(new SlotThorium(te, TEBreeder.Slots.REACTANT.ordinal(), 35, 30));
		addSlotToContainer(new SlotOutput(te, 116, 30));
	}
	private void addPlayerSlots(IInventory playerInv) {
		int slotY;
		for (slotY = 0; slotY < 3; slotY++) {
			for (int slotX = 0; slotX < 9; slotX++) {
				addSlotToContainer(new Slot(playerInv, slotX + slotY * 9 + 9, 8 + slotX * 18, 84 + slotY * 18));
			}
		}
		for (slotY = 0; slotY < 9; slotY++) {
			addSlotToContainer(new Slot(playerInv, slotY, 8 + slotY * 18, 142));
		}
    }
	
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		int invSize = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).getSlots();
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (index < invSize) {
                if (!this.mergeItemStack(itemstack1, invSize, this.inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(itemstack1, 0, invSize, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }
}
