package landmaster.landcraft.container;

import landmaster.landcore.api.*;
import landmaster.landcraft.net.*;
import landmaster.landcraft.net.teupdate.*;
import landmaster.landcraft.tile.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;

public class ContTEPlayerMime extends ContEnergy {
	private int energy = 0;
	
	public ContTEPlayerMime(EntityPlayer player, TEPlayerMime te) {
		super(player, te);
		addPlayerSlots(player.inventory);
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
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		if (energy != te.getEnergyStored(null)) {
			if (player instanceof EntityPlayerMP) {
				energy = te.getEnergyStored(null);
				PacketHandler.INSTANCE.sendTo(new PacketUpdateTE(new Coord4D(te), new UpdateTEPlayerMime(energy)), (EntityPlayerMP)player);
			}
		}
	}
}
