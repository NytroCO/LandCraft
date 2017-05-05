package landmaster.landcore.api.item;

import landmaster.landcore.api.block.*;
import mcjty.lib.compat.*;
import net.minecraft.block.*;
import net.minecraft.item.*;

public class ItemBlockMeta extends CompatItemBlock {
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
