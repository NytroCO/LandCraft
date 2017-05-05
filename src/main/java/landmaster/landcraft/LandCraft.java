package landmaster.landcraft;

import org.apache.commons.lang3.*;

import landmaster.landcore.api.item.*;
import landmaster.landcraft.api.*;
import landmaster.landcraft.block.*;
import landmaster.landcraft.config.*;
import landmaster.landcraft.gui.proxy.*;
import landmaster.landcraft.item.*;
import landmaster.landcraft.net.*;
import landmaster.landcraft.proxy.*;
import landmaster.landcraft.tile.*;
import landmaster.landcraft.util.*;
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
	public static final String DEPENDS = "required-after:landcore@[1.3.3.0,);required-after:compatlayer;after:OpenComputers";
	
	@Instance(MODID)
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
	
	public static final BlockLandiaPortalMarker landia_portal_marker = new BlockLandiaPortalMarker();
	
	public static final ItemWrench wrench = new ItemWrench();
	public static final Item redstone_component = new CompatItem().setUnlocalizedName("redstone_component").setRegistryName("redstone_component").setCreativeTab(creativeTab);
	
	
	// METALS
	
	public static final BlockLandiaOre landia_ore = new BlockLandiaOre();
	public static final BlockLandiaMetal landia_metal = new BlockLandiaMetal();
	
	public static final ItemLandiaIngot landia_ingot = new ItemLandiaIngot();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		(config = new Config(event)).sync();
		
		initMachines();
		
		initMetals();
		
		ItemBlock landia_portal_marker_item = new ItemBlock(landia_portal_marker);
		GameRegistry.register(landia_portal_marker);
		GameRegistry.register(landia_portal_marker_item, landia_portal_marker.getRegistryName());
		proxy.registerItemRenderer(landia_portal_marker_item, 0, "landia_portal_marker");
		GameRegistry.registerTileEntity(TELandiaPortalMarker.class, MODID+"_landia_portal_marker");
		
		GameRegistry.register(redstone_component);
		proxy.registerItemRenderer(redstone_component, 0, "redstone_component");
		
		proxy.bindTESRs();
		
		LandcraftBiomes.init();
		
		LandiaWorldProvider.register();
	}
	
	private static void initMachines() {
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
	}
	
	private static void initMetals() {
		final LandiaOreType[] values = LandiaOreType.values();
		
		ItemBlock landia_ore_item = new ItemBlockMeta(landia_ore);
		GameRegistry.register(landia_ore);
		GameRegistry.register(landia_ore_item, landia_ore.getRegistryName());
		for (int i=0; i<values.length; ++i) {
			proxy.registerItemRenderer(landia_ore_item, i, landia_ore.getRegistryName().getResourcePath(), "type="+values[i]);
			OreDictionary.registerOre("ore"+StringUtils.capitalize(values[i].toString()),
					new ItemStack(landia_ore, 1, i));
		}
		
		ItemBlock landia_metal_item = new ItemBlockMeta(landia_metal);
		GameRegistry.register(landia_metal);
		GameRegistry.register(landia_metal_item, landia_metal.getRegistryName());
		for (int i=0; i<values.length; ++i) {
			proxy.registerItemRenderer(landia_metal_item, i, landia_metal.getRegistryName().getResourcePath(), "type="+values[i]);
			OreDictionary.registerOre("block"+StringUtils.capitalize(values[i].toString()),
					new ItemStack(landia_metal, 1, i));
		}
		
		GameRegistry.register(landia_ingot);
		for (int i=0; i<values.length; ++i) {
			proxy.registerItemRenderer(landia_ingot, i, "ingot/"+values[i]);
			OreDictionary.registerOre("ingot"+StringUtils.capitalize(values[i].toString()),
					new ItemStack(landia_ingot, 1, i));
		}
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		PacketHandler.init();
		
		BreederFeedstock.addOreDict("ingotIron", 16, 200);
		BreederFeedstock.addOreDict("ingotTungsten", 64, 1000);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiProxy());
		
		initMachineRecipes();
		
		GameRegistry.addRecipe(new ShapedOreRecipe(landia_portal_marker,
				" d ", "lnl", "aea",
				'd', "gemDiamond", 'l', "ingotLandium",
				'n', Items.ENDER_PEARL, 'a', "ingotGold",
				'e', "gemEmerald"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(redstone_component,
				"fdh",
				'f', "ingotIron", 'd', "dustRedstone",
				'h', "ingotThorium"));
		
		final LandiaOreType[] values = LandiaOreType.values();
		for (int i=0; i<values.length; ++i) {
			final String ingotName = "ingot"+StringUtils.capitalize(values[i].toString());
			
			GameRegistry.addSmelting(new ItemStack(landia_ore, 1, i),
					new ItemStack(landia_ingot, 1, i), 0.85f);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(landia_metal, 1, i),
					"III", "III", "III",
					'I', ingotName));
			GameRegistry.addShapelessRecipe(new ItemStack(landia_ingot, 9, i), new ItemStack(landia_metal, 1, i));
		}
	}
	
	private static void initMachineRecipes() {
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
	}
}
