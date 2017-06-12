package landmaster.landcraft.container;

import landmaster.landcraft.tile.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;

public class ContEnergy extends Container {
	protected final TEEnergy te;
	protected final EntityPlayer player;
	
	public ContEnergy(EntityPlayer player, TEEnergy te) {
		this.te = te;
		this.player = player;
	}
	public TEEnergy getTE() {
		return te;
	}
	public EntityPlayer getPlayer() {
		return player;
	}
	public IInventory getPlayerInv() {
		return player.inventory;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}
