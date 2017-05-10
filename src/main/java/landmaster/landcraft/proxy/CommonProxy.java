package landmaster.landcraft.proxy;

import landmaster.landcraft.tile.*;
import net.minecraft.block.*;
import net.minecraft.block.properties.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;

public class CommonProxy {
	public void registerItemRenderer(Item item, int meta, String id) {
	}
	
	public void registerItemRenderer(Item item, int meta, String id, String variant) {
	}
	
	public <T extends TileEntity> void bindTESR(Class<T> clazz, ITESRProvider<T> provider) {
	}
	
	public void setCustomStateMapper(Block block, IProperty<?>... ignore) {
	}
	
	public void initColorHandlers() {
	}
}
