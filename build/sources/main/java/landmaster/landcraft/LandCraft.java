package landmaster.landcraft;

import landmaster.landcraft.api.*;
import landmaster.landcraft.block.*;
import landmaster.landcraft.gui.proxy.*;
import landmaster.landcraft.net.*;
import landmaster.landcraft.proxy.*;
import landmaster.landcraft.tile.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.fml.common.registry.*;
import net.minecraftforge.oredict.*;

@Mod(modid = LandCraft.MODID, name = LandCraft.NAME, version = LandCraft.VERSION, dependencies = LandCraft.DEPENDS)
public class LandCraft {
	public static final String MODID = "landcraft";
	public static final String NAME = "LandCraft";
	public static final String VERSION = "1.0";
	public static final String DEPENDS = "required-after:landcore";
	
	@Mod.Instance(MODID)
	public static LandCraft INSTANCE;
	
	@SidedProxy(serverSide = "landmaster.landcraft.proxy.CommonProxy", clientSide = "landmaster.landcraft.proxy.ClientProxy")
	public static CommonProxy proxy;
	
	public static final BlockBreeder breeder = new BlockBreeder();
	public static final BlockPlayerMime player_mime = new BlockPlayerMime();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ItemBlock breeder_item = new ItemBlock(breeder);
		GameRegistry.register(breeder);
		GameRegistry.register(breeder_item, breeder.getRegistryName());
		proxy.registerItemRenderer(breeder_item, 0, "breeder");
		
		ItemBlock player_mime_item = new ItemBlock(player_mime);
		GameRegistry.register(player_mime);
		GameRegistry.register(player_mime_item, player_mime.getRegistryName());
		proxy.registerItemRenderer(player_mime_item, 0, "player_mime");
		
		GameRegistry.registerTileEntity(TEBreeder.class, MODID+"_breeder_reactor");
		GameRegistry.registerTileEntity(TEPlayerMime.class, MODID+"_player_mime");
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		PacketHandler.init();
		
		BreederFeedstock.addOreDict("ingotIron", 16, 40);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiProxy());
		
		GameRegistry.addRecipe(new ShapedOreRecipe(breeder,
				"wdw", "wFw", "hhh",
				'w', "ingotTungsten", 'h', "ingotThorium",
				'd', "gemDiamond", 'F', Blocks.FURNACE));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(player_mime,
				" S ", "hdh", "www",
				'S', Items.DIAMOND_SWORD, 'h', "ingotThorium",
				'd', "gemDiamond", 'w', "ingotTungsten"));
	}
}
