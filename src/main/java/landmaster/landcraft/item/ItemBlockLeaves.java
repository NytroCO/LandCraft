package landmaster.landcraft.item;

import landmaster.landcore.api.block.*;
import landmaster.landcore.api.item.*;
import net.minecraft.block.*;

public class ItemBlockLeaves extends ItemBlockMeta {
	public <T extends Block & IMetaBlockName> ItemBlockLeaves(T block) {
		super(block);
	}
	
	@Override
    public int getMetadata(int damage) {
        return damage | 4;
    }
}
