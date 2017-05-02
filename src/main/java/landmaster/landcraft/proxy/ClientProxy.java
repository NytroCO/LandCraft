package landmaster.landcraft.proxy;

import landmaster.landcraft.*;
import landmaster.landcraft.tile.*;
import landmaster.landcraft.tile.render.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.item.*;
import net.minecraftforge.client.model.*;
import net.minecraftforge.fml.client.registry.*;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
	    ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(LandCraft.MODID + ":" + id, "inventory"));
	}
	
	@Override
	public void bindTESRs() {
		ClientRegistry.bindTileEntitySpecialRenderer(TELandiaPortalMarker.class, new TESRLandiaPortalMarker());
	}
}
