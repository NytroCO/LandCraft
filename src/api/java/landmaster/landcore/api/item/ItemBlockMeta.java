package landmaster.landcore.api.item;

import landmaster.landcore.api.block.*;
import net.minecraft.block.*;
import net.minecraft.item.*;

public class ItemBlockMeta extends ItemBlock {
	public <T extends Block & IMetaBlockName> ItemBlockMeta(T block) {
		super(block);
        setMaxDamage(0);
        setHasSubtypes(true);
	}
	
	@Override
	public int getMetadata(int damage) {
		return damage;
	}
	
	@Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + ((IMetaBlockName)block).getSpecialName(stack);
    }
}
