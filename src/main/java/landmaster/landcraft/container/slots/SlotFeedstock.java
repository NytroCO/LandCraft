package landmaster.landcraft.container.slots;

import landmaster.landcraft.api.*;
import landmaster.landcraft.tile.*;
import net.minecraft.item.*;

public class SlotFeedstock extends SlotItemHandlerBase {
	public SlotFeedstock(TEBreeder te, int xPosition, int yPosition) {
		super(te, TEBreeder.Slots.FEEDSTOCK.ordinal(), xPosition, yPosition);
	}
	@Override
    public boolean isItemValid(ItemStack stack) {
		return BreederFeedstock.getMass(stack) > 0;
	}
}
