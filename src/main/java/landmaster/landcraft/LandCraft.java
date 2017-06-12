package landmaster.landcraft;

import java.util.*;

import org.apache.commons.lang3.*;
import org.apache.logging.log4j.*;

import landmaster.landcore.api.item.*;
import landmaster.landcraft.api.*;
import landmaster.landcraft.block.*;
import landmaster.landcraft.config.*;
import landmaster.landcraft.content.*;
import landmaster.landcraft.gui.proxy.*;
import landmaster.landcraft.item.*;
import landmaster.landcraft.proxy.*;
import landmaster.landcraft.tile.*;
import landmaster.landcraft.util.*;
import landmaster.landcraft.world.*;
import landmaster.landcraft.world.biome.*;
import landmaster.landcraft.world.gen.*;
import mcjty.lib.compat.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.*;
import net.minecraftforge.fluids.*;
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
	public static final String VERSION = "1.3.0.0";
	public static final String DEPENDS = "required-after:landcore@[1.3.5.0,);required-after:compatlayer@[0.2.8,);"
			+ "after:OpenComputers;after:opencomputers;after:JEI;after:jei";
	
	@Instance(MODID)
	public static LandCraft INSTANCE;
	
	public static Config config;
	
	public static final Logger log = LogManager.getLogger(
			MODID.toUpperCase(Locale.US/* to avoid problems with Turkish */));
	
	@SidedProxy(serverSide = "landmaster.landcraft.proxy.CommonProxy", clientSide = "landmaster.landcraft.proxy.ClientProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		(config = new Config(event)).sync();
		
		proxy.initEntities();
		
		initNature();
		
		initAgriculture();
		
		initMachines();
		
		initMetals();
		
		GameRegistry.register(LandCraftContent.landmasters_wings);
		proxy.registerItemRenderer(LandCraftContent.landmasters_wings, 0, "landmasters_wings");
		
		ItemBlock landia_portal_marker_item = new CompatItemBlock(LandCraftContent.landia_portal_marker);
		GameRegistry.register(LandCraftContent.landia_portal_marker);
		GameRegistry.register(landia_portal_marker_item, LandCraftContent.landia_portal_marker.getRegistryName());
		proxy.registerItemRenderer(landia_portal_marker_item, 0, "landia_portal_marker");
		GameRegistry.registerTileEntity(TELandiaPortalMarker.class, MODID+"_landia_portal_marker");
		
		ItemBlock landia_tower_item = new CompatItemBlock(LandCraftContent.landia_tower) {
			@Override
			protected EnumActionResult clOnItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
				return LandCraftContent.landia_tower.testBlockPlacement(world, pos) ? super.clOnItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ) : EnumActionResult.FAIL;
			}
		};
		GameRegistry.register(LandCraftContent.landia_tower);
		GameRegistry.register(landia_tower_item, LandCraftContent.landia_tower.getRegistryName());
		proxy.registerItemRenderer(landia_tower_item, 0, "landia_tower");
		GameRegistry.registerTileEntity(TELandiaTower.class, MODID+"_landia_tower");
		
		proxy.bindTESRs();
		
		GameRegistry.register(LandCraftContent.redstone_component);
		proxy.registerItemRenderer(LandCraftContent.redstone_component, 0, "redstone_component");
		
		LandcraftBiomes.init();
		
		LandiaWorldProvider.register();
		
		initWorldgen();
	}
	
	private static void initWorldgen() {
		GameRegistry.registerWorldGenerator(new LandiaOreWorldgen(), 10);
		GameRegistry.registerWorldGenerator(new CinnamonWorldgen(), 50);
		
		GameRegistry.registerWorldGenerator(new OnionWorldgen(), 28);
		GameRegistry.registerWorldGenerator(new RiceWorldgen(), 33);
	}
	
	private static void initAgriculture() {
		GameRegistry.register(LandCraftContent.wild_onion);
		GameRegistry.register(LandCraftContent.onion_crop);
		GameRegistry.register(LandCraftContent.onion);
		OreDictionary.registerOre("cropOnion", LandCraftContent.onion);
		OreDictionary.registerOre("seedOnion", LandCraftContent.onion);
		proxy.registerItemRenderer(LandCraftContent.onion, 0, "onion");
		
		GameRegistry.register(LandCraftContent.wild_rice);
		GameRegistry.register(LandCraftContent.rice_crop);
		GameRegistry.register(LandCraftContent.rice);
		OreDictionary.registerOre("cropRice", LandCraftContent.rice);
		OreDictionary.registerOre("seedRice", LandCraftContent.rice);
		proxy.registerItemRenderer(LandCraftContent.rice, 0, "rice");
		
		GameRegistry.register(LandCraftContent.potato_onion_pastry);
		proxy.registerItemRenderer(LandCraftContent.potato_onion_pastry, 0, "potato_onion_pastry");
		proxy.registerItemRenderer(LandCraftContent.potato_onion_pastry, 1, "potato_onion_pastry_cooked");
		
		GameRegistry.register(LandCraftContent.pho);
		proxy.registerItemRenderer(LandCraftContent.pho, 0, "pho");
		proxy.registerItemRenderer(LandCraftContent.pho, 1, "pho_full");
	}
	
	private static void initNature() {
		ItemBlockMeta landia_sapling_item = new ItemBlockMeta(LandCraftContent.landia_sapling);
		GameRegistry.register(LandCraftContent.landia_sapling);
		GameRegistry.register(landia_sapling_item, LandCraftContent.landia_sapling.getRegistryName());
		proxy.setCustomStateMapper(LandCraftContent.landia_sapling, BlockSapling.STAGE, BlockSapling.TYPE);
		for (LandiaTreeType type: LandiaTreeType.values()) {
			proxy.registerItemRenderer(landia_sapling_item, type.ordinal(), "landia_sapling_"+type);
			OreDictionary.registerOre("treeSapling", new ItemStack(LandCraftContent.landia_sapling, 1, type.ordinal()));
		}
		
		ItemBlockLeaves landia_leaves_item = new ItemBlockLeaves(LandCraftContent.landia_leaves);
		GameRegistry.register(LandCraftContent.landia_leaves);
		GameRegistry.register(landia_leaves_item, LandCraftContent.landia_leaves.getRegistryName());
		proxy.setCustomStateMapper(LandCraftContent.landia_leaves, BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE);
		for (LandiaTreeType type: LandiaTreeType.values()) {
			proxy.registerItemRenderer(landia_leaves_item, type.ordinal(), "landia_leaves",
					String.format(Locale.US, "%s=%s", LandiaTreeType.L_TYPE.getName(), type));
			OreDictionary.registerOre("treeLeaves", new ItemStack(LandCraftContent.landia_leaves, 1, type.ordinal()));
		}
		
		ItemBlockMeta landia_log_item = new ItemBlockMeta(LandCraftContent.landia_log);
		GameRegistry.register(LandCraftContent.landia_log);
		GameRegistry.register(landia_log_item, LandCraftContent.landia_log.getRegistryName());
		for (LandiaTreeType type: LandiaTreeType.values()) {
			String variant = String.format(Locale.US, "%s=%s,%s=%s",
					Axis.AXIS.getName(), Axis.Y,
					LandiaTreeType.L_TYPE.getName(), type);
			proxy.registerItemRenderer(landia_log_item, type.ordinal(), "landia_log", variant);
			OreDictionary.registerOre("logWood", new ItemStack(LandCraftContent.landia_log, 1, type.ordinal()));
		}
		
		ItemBlockMeta landia_planks_item = new ItemBlockMeta(LandCraftContent.landia_planks);
		GameRegistry.register(LandCraftContent.landia_planks);
		GameRegistry.register(landia_planks_item, LandCraftContent.landia_planks.getRegistryName());
		for (LandiaTreeType type: LandiaTreeType.values()) {
			proxy.registerItemRenderer(landia_planks_item,
					type.ordinal(), "landia_planks",
					String.format(Locale.US, "%s=%s", LandiaTreeType.L_TYPE.getName(), type));
			OreDictionary.registerOre("plankWood", new ItemStack(LandCraftContent.landia_planks, 1, type.ordinal()));
		}
		
		GameRegistry.register(LandCraftContent.cinnamon_bark);
		
		GameRegistry.register(LandCraftContent.cinnamon);
		proxy.registerItemRenderer(LandCraftContent.cinnamon, 0, "cinnamon");
		OreDictionary.registerOre("cropCinnamon", LandCraftContent.cinnamon);
		
		ItemBlockSlab<LandiaTreeType> landia_wood_slab_item = new ItemBlockSlab<>(LandCraftContent.landia_wood_slab);
		GameRegistry.register(LandCraftContent.landia_wood_slab);
		GameRegistry.register(landia_wood_slab_item, LandCraftContent.landia_wood_slab.getRegistryName());
		for (LandiaTreeType type: LandiaTreeType.values()) {
			String variant = String.format(Locale.US, "%s=%s,%s=%s",
					BlockSlab.HALF.getName(), BlockSlab.EnumBlockHalf.BOTTOM,
					LandiaTreeType.L_TYPE.getName(), type);
			proxy.registerItemRenderer(landia_wood_slab_item,
					type.ordinal(), "landia_wood_slab",
					variant);
			OreDictionary.registerOre("slabWood", new ItemStack(LandCraftContent.landia_wood_slab, 1, type.ordinal()));
		}
		
		for (LandiaTreeType type: LandiaTreeType.values()) {
			BlockModStairs block = GameRegistry.register(new BlockModStairs(
					LandCraftContent.landia_log.getDefaultState()
					.withProperty(LandiaTreeType.L_TYPE, type),
					type+"_stairs"));
			LandCraftContent.landia_wood_stairs.put(type, block);
			ItemBlock ib = new CompatItemBlock(block);
			GameRegistry.register(ib, block.getRegistryName());
			proxy.registerItemRenderer(ib, 0, block.getRegistryName().getResourcePath());
			OreDictionary.registerOre("stairWood", block);
		}
	}
	
	private static void initMachines() {
		if (Config.breeder) {
			ItemBlock breeder_item = new CompatItemBlock(LandCraftContent.breeder);
			GameRegistry.register(LandCraftContent.breeder);
			GameRegistry.register(breeder_item, LandCraftContent.breeder.getRegistryName());
			proxy.registerItemRenderer(breeder_item, 0, "breeder");
			GameRegistry.registerTileEntity(TEBreeder.class, MODID+"_breeder_reactor");
		}
		
		if (Config.player_mime) {
			ItemBlock player_mime_item = new CompatItemBlock(LandCraftContent.player_mime);
			GameRegistry.register(LandCraftContent.player_mime);
			GameRegistry.register(player_mime_item, LandCraftContent.player_mime.getRegistryName());
			proxy.registerItemRenderer(player_mime_item, 0, "player_mime");
			GameRegistry.registerTileEntity(TEPlayerMime.class, MODID+"_player_mime");
		}
		
		if (Config.thorium_generator) {
			ItemBlock thorium_generator_item = new CompatItemBlock(LandCraftContent.thorium_generator);
			GameRegistry.register(LandCraftContent.thorium_generator);
			GameRegistry.register(thorium_generator_item, LandCraftContent.thorium_generator.getRegistryName());
			proxy.registerItemRenderer(thorium_generator_item, 0, "thorium_generator");
			GameRegistry.registerTileEntity(TEThoriumGenerator.class, MODID+"_thorium_generator");
		}
		
		if (Config.pot) {
			ItemBlock pot_item = new CompatItemBlock(LandCraftContent.pot);
			GameRegistry.register(LandCraftContent.pot);
			GameRegistry.register(pot_item, LandCraftContent.pot.getRegistryName());
			proxy.registerItemRenderer(pot_item, 0, "pot");
			GameRegistry.registerTileEntity(TEPot.class, MODID+"_pot");
		}
		
		if (Config.wrench) {
			GameRegistry.register(LandCraftContent.wrench);
			proxy.registerItemRenderer(LandCraftContent.wrench, 0, "item_wrench");
		}
	}
	
	private static void initMetals() {
		final LandiaOreType[] values = LandiaOreType.values();
		
		ItemBlock landia_ore_item = new ItemBlockMeta(LandCraftContent.landia_ore);
		GameRegistry.register(LandCraftContent.landia_ore);
		GameRegistry.register(landia_ore_item, LandCraftContent.landia_ore.getRegistryName());
		for (int i=0; i<values.length; ++i) {
			proxy.registerItemRenderer(landia_ore_item, i, LandCraftContent.landia_ore.getRegistryName().getResourcePath(), "type="+values[i]);
			OreDictionary.registerOre("ore"+StringUtils.capitalize(values[i].toString()),
					new ItemStack(LandCraftContent.landia_ore, 1, i));
		}
		
		ItemBlock landia_metal_item = new ItemBlockMeta(LandCraftContent.landia_metal);
		GameRegistry.register(LandCraftContent.landia_metal);
		GameRegistry.register(landia_metal_item, LandCraftContent.landia_metal.getRegistryName());
		for (int i=0; i<values.length; ++i) {
			proxy.registerItemRenderer(landia_metal_item, i, LandCraftContent.landia_metal.getRegistryName().getResourcePath(), "type="+values[i]);
			OreDictionary.registerOre("block"+StringUtils.capitalize(values[i].toString()),
					new ItemStack(LandCraftContent.landia_metal, 1, i));
		}
		
		GameRegistry.register(LandCraftContent.landia_ingot);
		for (int i=0; i<values.length; ++i) {
			proxy.registerItemRenderer(LandCraftContent.landia_ingot, i, "ingot/"+values[i]);
			OreDictionary.registerOre("ingot"+StringUtils.capitalize(values[i].toString()),
					new ItemStack(LandCraftContent.landia_ingot, 1, i));
		}
		
		for (LandiaOreType type: values) {
			LandCraftContent.toolsMap.put(type, Utils.registerTools(type,
					EnumHelper.addToolMaterial(
							type.name(), type.getLevel()+1, type.getDurability(),
							type.getEfficiency(), type.getDamage(), type.getEnchantability()),
					type.getAxeDamage(), type.getAxeSpeed()));
		}
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.initColorHandlers();
		
		BreederFeedstock.addOreDict("ingotIron", 16, 200);
		BreederFeedstock.addOreDict("ingotTungsten", 64, 1000);
		BreederFeedstock.addOreDict("ingotLandium", 100, 1200);
		
		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiProxy());
		
		initNatureRecipes();
		
		initAgricultureRecipes();
		
		initMachineRecipes();
		
		GameRegistry.addRecipe(new ShapedOreRecipe(LandCraftContent.landia_tower,
				" K ", "GLM", "FLR", 'K', "blockKelline", 'G', "blockGarfax",
				'M', "blockMorganine", 'R', "blockRacheline", 'F', "blockFriscion",
				'L', "blockLandium"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(LandCraftContent.landia_portal_marker,
				" d ", "lnl", "aea",
				'd', "gemDiamond", 'l', "ingotLandium",
				'n', Items.ENDER_PEARL, 'a', "ingotGold",
				'e', "gemEmerald"));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(LandCraftContent.redstone_component,
				"fdh",
				'f', "ingotIron", 'd', "dustRedstone",
				'h', "ingotThorium"));
		
		final LandiaOreType[] values = LandiaOreType.values();
		for (int i=0; i<values.length; ++i) {
			final String ingotName = "ingot"+StringUtils.capitalize(values[i].toString());
			
			GameRegistry.addSmelting(new ItemStack(LandCraftContent.landia_ore, 1, i),
					new ItemStack(LandCraftContent.landia_ingot, 1, i), 1.25f);
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(LandCraftContent.landia_metal, 1, i),
					"III", "III", "III",
					'I', ingotName));
			GameRegistry.addShapelessRecipe(new ItemStack(LandCraftContent.landia_ingot, 9, i), new ItemStack(LandCraftContent.landia_metal, 1, i));
		}
		
		for (Utils.ToolGroup group: LandCraftContent.toolsMap.values()) {
			group.initRecipes();
		}
	}
	
	private static void initAgricultureRecipes() {
		GameRegistry.addRecipe(new ShapelessOreRecipe(LandCraftContent.potato_onion_pastry,
				"cropPotato", "cropOnion", "cropWheat", "cropWheat"));
		GameRegistry.addSmelting(new ItemStack(LandCraftContent.potato_onion_pastry),
				new ItemStack(LandCraftContent.potato_onion_pastry, 1, 1), 0.35f);
		
		PotRecipes.addRecipe(PotRecipes.RecipePOredict.of(
				"cropCinnamon", "cropOnion", "bone", new FluidStack(FluidRegistry.WATER, 1),
				new PotRecipes.RecipeOutput(new ItemStack(LandCraftContent.pho), 4, 5, 1200)));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(LandCraftContent.pho,1,1),
				new ItemStack(LandCraftContent.pho), "cropRice", "cropRice", "cropRice", Items.BOWL, Items.BEEF));
	}
	
	private static void initNatureRecipes() {
		final LandiaTreeType[] values = LandiaTreeType.values();
		for (int i=0; i<values.length; ++i) {
			GameRegistry.addShapelessRecipe(new ItemStack(LandCraftContent.landia_planks, 4, i), new ItemStack(LandCraftContent.landia_log, 1, i));
			GameRegistry.addShapedRecipe(new ItemStack(LandCraftContent.landia_wood_slab, 6, i), "ppp", 'p', new ItemStack(LandCraftContent.landia_planks, 1, i));
			GameRegistry.addShapedRecipe(new ItemStack(LandCraftContent.landia_wood_stairs.get(values[i]), 4), "p  ", "pp ", "ppp", 'p', new ItemStack(LandCraftContent.landia_planks, 1, i));
			
			GameRegistry.addSmelting(new ItemStack(LandCraftContent.landia_log, 1, i), new ItemStack(Items.COAL, 1, 1), 0.15f);
		}
	}
	
	private static void initMachineRecipes() {
		if (Config.breeder) {
			GameRegistry.addRecipe(new ShapedOreRecipe(LandCraftContent.breeder,
					"wdw", "wFw", "hlh",
					'w', "ingotTungsten", 'h', "ingotThorium",
					'd', "gemDiamond", 'F', Blocks.FURNACE,
					'l', "ingotLandium"));
		}
		
		if (Config.player_mime) {
			GameRegistry.addRecipe(new ShapedOreRecipe(LandCraftContent.player_mime,
					"rSr", "hOh", "www",
					'S', Items.DIAMOND_SWORD, 'h', "ingotThorium",
					'O', Blocks.OBSIDIAN, 'w', "ingotTungsten",
					'r', LandCraftContent.redstone_component));
		}
		
		if (Config.thorium_generator) {
			GameRegistry.addRecipe(new ShapedOreRecipe(LandCraftContent.thorium_generator,
					"rzr", "wFw", "ara",
					'w', "ingotTungsten", 'r', LandCraftContent.redstone_component,
					'F', Blocks.FURNACE, 'z', "gemLapis", 'a', "ingotGold"));
		}
		
		if (Config.pot) {
			GameRegistry.addRecipe(new ShapedOreRecipe(LandCraftContent.pot,
					"m m", "lFl", "lll",
					'm', "ingotMorganine", 'l', "ingotLandium",
					'F', Blocks.FURNACE));
		}
		
		if (Config.wrench) {
			GameRegistry.addRecipe(new ShapedOreRecipe(LandCraftContent.wrench,
					"w", "f", "z",
					'w', "ingotTungsten", 'f', "ingotIron",
					'z', "gemLapis"));
		}
	}
}
