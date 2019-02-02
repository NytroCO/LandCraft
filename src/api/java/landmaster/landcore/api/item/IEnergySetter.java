package landmaster.landcore.api.item;

import net.minecraft.item.*;

interface IEnergySetter {
	void setEnergyStored(ItemStack is, int energy);
}
