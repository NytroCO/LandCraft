package landmaster.landcraft.container.slots;

import org.apache.commons.lang3.*;

import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.oredict.*;

public class SlotThorium extends SlotItemHandlerBase {
	public SlotThorium(TileEntity te, int slot, int xPosition, int yPosition) {
		super(te, slot, xPosition, yPosition);
	}
	@Override
    public boolean isItemValid(ItemStack stack) {
        return ArrayUtils.contains(OreDictionary.getOreIDs(stack), OreDictionary.getOreID("ingotThorium"));
    }
}
