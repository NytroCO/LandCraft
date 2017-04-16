package landmaster.landcraft.container.slots;

import org.apache.commons.lang3.*;

import landmaster.landcraft.tile.*;
import net.minecraft.item.*;
import net.minecraftforge.oredict.*;

public class SlotReactant extends SlotItemHandlerBase {
	public SlotReactant(TEBreeder te, int xPosition, int yPosition) {
		super(te, TEBreeder.Slots.REACTANT.ordinal(), xPosition, yPosition);
	}
	@Override
    public boolean isItemValid(ItemStack stack) {
        return ArrayUtils.contains(OreDictionary.getOreIDs(stack), OreDictionary.getOreID("ingotThorium"));
    }
}
