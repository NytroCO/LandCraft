package landmaster.landcraft;

import landmaster.landcraft.api.*;
import landmaster.landcraft.block.*;
import landmaster.landcraft.config.*;
import landmaster.landcraft.gui.proxy.*;
import landmaster.landcraft.item.*;
import landmaster.landcraft.net.*;
import landmaster.landcraft.proxy.*;
import landmaster.landcraft.tile.*;
import landmaster.landcraft.world.*;
import mcjty.lib.compat.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.fml.common.registry.*;
import net.minecraftforge.oredict.*;

@Mod(modid = LandCraft.MODID, name = LandCraft.NAME, version = LandCraft.VERSION, dependencies = LandCraft.DEPENDS, useMetadata = true, acceptedMinecraftVersions = "[1.9,1.12)")
public class LandCraft {
	public static final String MODID = "landcraft";
	public static final String NAME = "Land Craft";
	public static final String VERSION = "1.0.0.1";
	public static final String DEPENDS = "required-after:landcore@[1.3.1.0,);required-after:compatlayer;after:OpenComputers";
	
	@Mod.Instance(MODID)
	public static LandCraft INSTANCE;
	
	public static Config config;
	
	@SidedProxy(serverSide = "landmaster.landcraft.proxy.CommonProxy", clientSide = "landmaster.landcraft.proxy.ClientProxy")
	public static CommonProxy proxy;
	
	public static final CompatCreativeTabs creativeTab = new CompatCreativeTabs(MODID) {
		@Override
		public Item getItem() {
			return redstone_component;
		}
	};
	
	public static final BlockBreeder breeder = new BlockBreeder();
	public static final BlockPlayerMime player_mime = new BlockPlayerMime();
	public static final BlockThoriumGenerator thorium_generator = new BlockThoriumGenerator();
	
	public static final ItemWrench wrench = new ItemWrench();
	
	public static final Item redstone_component = new CompatItem().setUnlocalizedName("redstone_component").setRegistryName("redstone_component").setCreativeTab(creativeTab);
	
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
		
		if (Config.wrench) {
			GameRegistry.register(wrench);
			proxy.registerItemRenderer(wrench, 0, "item_wrench");
		}
		
		GameRegistry.register(redstone_component);
		proxy.registerItemRenderer(redstone_component, 0, "redstone_component");
		
		LandcraftBiomes.init();
		
		LandiaWorldProvider.register();
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
					"rSr", "hOh", "www",
					'S', Items.DIAMOND_SWORD, 'h', "ingotThorium",
					'O', Blocks.OBSIDIAN, 'w', "ingotTungsten",
					'r', redstone_component));
		}
		
		if (Config.thorium_generator) {
			GameRegistry.addRecipe(new ShapedOreRecipe(thorium_generator,
					"rzr", "wFw", "ara",
					'w', "ingotTungsten", 'r', redstone_component,
					'F', Blocks.FURNACE, 'z', "gemLapis", 'a', "ingotGold"));
		}
		
		if (Config.wrench) {
			GameRegistry.addRecipe(new ShapedOreRecipe(wrench,
					"w", "f", "z",
					'w', "ingotTungsten", 'f', "ingotIron",
					'z', "gemLapis"));
		}
		
		GameRegistry.addRecipe(new ShapedOreRecipe(redstone_component,
				"fdh",
				'f', "ingotIron", 'd', "dustRedstone",
				'h', "ingotThorium"));
	}
}
