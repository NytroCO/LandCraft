package landmaster.landcraft.container;

import landmaster.landcore.api.*;
import landmaster.landcraft.container.slots.*;
import landmaster.landcraft.net.*;
import landmaster.landcraft.net.teupdate.*;
import landmaster.landcraft.tile.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.items.*;

public class ContTEThoriumGenerator extends ContEnergy {
	private int energy = 0, progress = 0;
	private FluidStack fs;
	private TEThoriumGenerator te;
	
	public ContTEThoriumGenerator(EntityPlayer player, TEThoriumGenerator te) {
		super(player, te);
		this.te = te;
		addOwnSlots();
		addPlayerSlots(player.inventory);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		FluidStack nfs = te.getFluid();
		if (progress != te.getProgress()
				|| (fs != null && !fs.isFluidStackIdentical(nfs)) || (fs == null && nfs != null)
				|| (energy != te.getEnergyStored(null))) {
			if (this.player instanceof EntityPlayerMP) {
				energy = te.getEnergyStored(null);
				progress = te.getProgress();
				fs = nfs;
				if (fs != null) fs = fs.copy();
				PacketHandler.INSTANCE.sendTo(new PacketUpdateTE(new Coord4D(te), new UpdateTEThoriumGenerator(energy, progress, fs)),
						(EntityPlayerMP)player);
			}
		}
	}
	
	private void addOwnSlots() {
		addSlotToContainer(new SlotThorium(te, 0, 35, 30));
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
