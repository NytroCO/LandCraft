package landmaster.landcore.api.item;

import net.minecraft.item.*;

public interface IEnergySetter {
	void setEnergyStored(ItemStack is, int energy);
}
