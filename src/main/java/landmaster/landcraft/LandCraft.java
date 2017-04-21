package landmaster.landcraft;

import landmaster.landcraft.api.*;
import landmaster.landcraft.block.*;
import landmaster.landcraft.config.*;
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
	public static final String VERSION = "1.0.0.0";
	public static final String DEPENDS = "required-after:landcore@[1.3.0.0,)";
	
	@Mod.Instance(MODID)
	public static LandCraft INSTANCE;
	
	public static Config config;
	
	@SidedProxy(serverSide = "landmaster.landcraft.proxy.CommonProxy", clientSide = "landmaster.landcraft.proxy.ClientProxy")
	public static CommonProxy proxy;
	
	public static final BlockBreeder breeder = new BlockBreeder();
	public static final BlockPlayerMime player_mime = new BlockPlayerMime();
	public static final BlockThoriumGenerator thorium_generator = new BlockThoriumGenerator();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		(config = new Config(event)).sync();
		
		if (Config.breeder) {
			ItemBlock breeder_item = new ItemBlock(breeder);
			GameRegistry.register(breeder);
			GameRegistry.register(breeder_item, breeder.getRegistryName());
			proxy.registerItemRenderer(breeder_item, 0, "breeder");
			GameRegistry.registerTileEntity(TEBreeder.class, MODID+"_breeder_reactor");
		}
		
		if (Config.player_mime) {
			ItemBlock player_mime_item = new ItemBlock(player_mime);
			GameRegistry.register(player_mime);
			GameRegistry.register(player_mime_item, player_mime.getRegistryName());
			proxy.registerItemRenderer(player_mime_item, 0, "player_mime");
			GameRegistry.registerTileEntity(TEPlayerMime.class, MODID+"_player_mime");
		}
		
		if (Config.thorium_generator) {
			ItemBlock thorium_generator_item = new ItemBlock(thorium_generator);
			GameRegistry.register(thorium_generator);
			GameRegistry.register(thorium_generator_item, thorium_generator.getRegistryName());
			proxy.registerItemRenderer(thorium_generator_item, 0, "thorium_generator");
			GameRegistry.registerTileEntity(TEThoriumGenerator.class, MODID+"_thorium_generator");
		}
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		PacketHandler.init();
		
		BreederFeedstock.addOreDict("ingotIron", 16, 200);
		BreederFeedstock.addOreDict("ingotTungsten", 64, 1000);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiProxy());
		
		if (Config.breeder) {
			GameRegistry.addRecipe(new ShapedOreRecipe(breeder,
					"wdw", "wFw", "hlh",
					'w', "ingotTungsten", 'h', "ingotThorium",
					'd', "gemDiamond", 'F', Blocks.FURNACE,
					'l', "ingotLandium"));
		}
		
		if (Config.player_mime) {
			GameRegistry.addRecipe(new ShapedOreRecipe(player_mime,
					" S ", "hOh", "www",
					'S', Items.DIAMOND_SWORD, 'h', "ingotThorium",
					'O', Blocks.OBSIDIAN, 'w', "ingotTungsten"));
		}
	}
}
