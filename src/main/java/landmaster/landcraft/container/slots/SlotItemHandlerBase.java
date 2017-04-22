package landmaster.landcraft.container.slots;

import mcjty.lib.compat.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.items.*;

public class SlotItemHandlerBase extends CompatSlotItemHandler {
	protected TileEntity te;
	
	public SlotItemHandlerBase(TileEntity te, int index, int xPosition, int yPosition) {
		super(te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null),
				index, xPosition, yPosition);
		this.te = te;
	}
	
	@Override
	public void onSlotChanged() {
		te.markDirty();
	}
}
