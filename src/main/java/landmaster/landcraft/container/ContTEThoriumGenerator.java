package landmaster.landcraft.container;

import landmaster.landcraft.container.slots.*;
import landmaster.landcraft.net.*;
import landmaster.landcraft.tile.*;
import mcjty.lib.compat.*;
import mcjty.lib.tools.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.items.*;

public class ContTEThoriumGenerator extends ContEnergy {
	private int progress = 0;
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
		if ((fs != null && !fs.isFluidStackIdentical(nfs)) || (fs == null && nfs != null)) {
			if (this.player instanceof EntityPlayerMP) {
				fs = nfs;
				if (fs != null) fs = fs.copy();
				PacketHandler.INSTANCE.sendTo(new PacketUpdateClientFluid(fs),
						(EntityPlayerMP)player);
			}
		}
		if (progress != te.getProgress()) {
			if (this.player instanceof EntityPlayerMP) {
				progress = te.getProgress();
				PacketHandler.INSTANCE.sendTo(new PacketUpdateTEThoriumGenerator(progress),
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
