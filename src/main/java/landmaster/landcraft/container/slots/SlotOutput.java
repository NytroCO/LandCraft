package landmaster.landcraft.container.slots;

import landmaster.landcraft.tile.*;
import net.minecraft.item.*;

public class SlotOutput extends SlotItemHandlerBase {
	public SlotOutput(TEBreeder te, int xPosition, int yPosition) {
		super(te, TEBreeder.Slots.OUTPUT.ordinal(), xPosition, yPosition);
	}
	
	public SlotOutput(TEPot te, int xPosition, int yPosition) {
		super(te, TEPot.Slots.OUT.ordinal(), xPosition, yPosition);
	}
	
	@Override
    public boolean isItemValid(ItemStack stack) {
		return false;
	}
}
