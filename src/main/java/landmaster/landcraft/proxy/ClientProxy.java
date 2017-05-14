package landmaster.landcraft.proxy;

import landmaster.landcraft.*;
import landmaster.landcraft.content.LandCraftContent;
import landmaster.landcraft.tile.*;
import landmaster.landcraft.util.*;
import net.minecraft.block.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.client.model.*;
import net.minecraftforge.fml.client.registry.*;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerItemRenderer(Item item, int meta, String id) {
	    registerItemRenderer(item, meta, id, "inventory");
	}
	
	@Override
	public void registerItemRenderer(Item item, int meta, String id, String variant) {
		ModelResourceLocation rl = new ModelResourceLocation(LandCraft.MODID + ":" + id, variant);
		if (meta >= 0)  {
			ModelLoader.setCustomModelResourceLocation(item, meta, rl);
		} else {
			ModelLoader.setCustomMeshDefinition(item, stack -> rl);
			ModelBakery.registerItemVariants(item, rl);
		}
	}
	
	@Override
	public <T extends TileEntity> void bindTESR(Class<T> clazz, ITESRProvider<T> provider) {
		ClientRegistry.bindTileEntitySpecialRenderer(clazz, provider.getRenderer());
	}
	
	@Override
	public void setCustomStateMapper(Block block, IProperty<?>... ignore) {
		ModelLoader.setCustomStateMapper(block, (new StateMap.Builder()).ignore(ignore).build());
	}
	
	@Override
	public void initColorHandlers() {
		Minecraft.getMinecraft().getBlockColors().registerBlockColorHandler((state, world, pos, index) -> {
			if (state.getValue(LandiaTreeType.L_TYPE) == LandiaTreeType.CINNAMON) {
				return 0x04961C;
			}
			return 0xFFFFFF;
		}, LandCraftContent.landia_leaves);
		
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler((stack, index) -> {
			@SuppressWarnings("deprecation")
            IBlockState state = ((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata());
            return Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, null, null, index);
		}, LandCraftContent.landia_leaves);
	}
}
