package landmaster.landcraft.container;

import landmaster.landcraft.tile.*;
import mcjty.lib.compat.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;

public class ContTEPlayerMime extends ContEnergy {
	public ContTEPlayerMime(EntityPlayer player, TEPlayerMime te) {
		super(player, te);
		addPlayerSlots(player.inventory);
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
}
