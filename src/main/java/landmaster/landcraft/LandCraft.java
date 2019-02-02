package landmaster.landcraft;

import java.util.*;
import java.util.stream.*;

import org.apache.commons.lang3.StringUtils;

import landmaster.landcore.api.*;
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
import landmaster.landcraft.world.gen.*;
import net.minecraft.block.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.common.util.*;
import net.minecraftforge.event.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.*;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.fml.common.registry.*;
import net.minecraftforge.oredict.*;

@Mod(modid = ModInfo.MODID, name = ModInfo.NAME, version = ModInfo.VERSION, dependencies = ModInfo.DEPENDS, useMetadata = true)
@EventBusSubscriber
public class LandCraft {
	@Instance(ModInfo.MODID)
	public static LandCraft INSTANCE;
	
	private static Config config;
	
	@SidedProxy(serverSide = "landmaster.landcraft.proxy.CommonProxy", clientSide = "landmaster.landcraft.proxy.ClientProxy")
	public static CommonProxy proxy;
	
	@SubscribeEvent
	public static void addBlocks(RegistryEvent.Register<Block> event) {
		event.getRegistry().register(LandCraftContent.landia_portal_marker);
		GameRegistry.registerTileEntity(TELandiaPortalMarker.class, ModInfo.MODID+"_landia_portal_marker");
		
		event.getRegistry().register(LandCraftContent.landia_tower);
		GameRegistry.registerTileEntity(TELandiaTower.class, ModInfo.MODID+"_landia_tower");
		
		event.getRegistry().register(LandCraftContent.wild_onion);
		event.getRegistry().register(LandCraftContent.onion_crop);
		
		event.getRegistry().register(LandCraftContent.wild_rice);
		event.getRegistry().register(LandCraftContent.rice_crop);
		
		event.getRegistry().register(LandCraftContent.tomato_crop);
		proxy.setCustomStateMapper(LandCraftContent.tomato_crop, BlockTomatoCrop.AGE);
		
		event.getRegistry().register(LandCraftContent.landia_sapling);
		proxy.setCustomStateMapper(LandCraftContent.landia_sapling, BlockSapling.STAGE, BlockSapling.TYPE);
		
		event.getRegistry().register(LandCraftContent.landia_leaves);
		proxy.setCustomStateMapper(LandCraftContent.landia_leaves, BlockLeaves.CHECK_DECAY, BlockLeaves.DECAYABLE);
		
		event.getRegistry().register(LandCraftContent.landia_log);
		
		event.getRegistry().register(LandCraftContent.landia_planks);
		
		event.getRegistry().register(LandCraftContent.cinnamon_bark);
		
		event.getRegistry().register(LandCraftContent.landia_wood_slab);
		
		for (LandiaTreeType type: LandiaTreeType.values()) {
			BlockModStairs block = new BlockModStairs(
					LandCraftContent.landia_log.getDefaultState()
					.withProperty(LandiaTreeType.L_TYPE, type),
					type+"_stairs");
			event.getRegistry().register(block);
			LandCraftContent.landia_wood_stairs.put(type, block);
		}
		
		if (Config.breeder) {
			event.getRegistry().register(LandCraftContent.breeder);
		}
		
		if (Config.player_mime) {
			event.getRegistry().register(LandCraftContent.player_mime);
		}
		
		if (Config.thorium_generator) {
			event.getRegistry().register(LandCraftContent.thorium_generator);
		}
		
		if (Config.pot) {
			event.getRegistry().register(LandCraftContent.pot);
		}
		
		event.getRegistry().register(LandCraftContent.landia_ore);
		
		event.getRegistry().register(LandCraftContent.landia_metal);
	}
	
	@SubscribeEvent
	public static void addItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(LandCraftContent.landmasters_wings);
		proxy.registerItemRenderer(LandCraftContent.landmasters_wings, 0, "landmasters_wings");
		
		event.getRegistry().register(LandCraftContent.weather_wand);
		proxy.registerItemRenderer(LandCraftContent.weather_wand, 0, "weather_wand");
		
		ItemBlock landia_portal_marker_item = new ItemBlock(LandCraftContent.landia_portal_marker);
		event.getRegistry().register(landia_portal_marker_item.setRegistryName(LandCraftContent.landia_portal_marker.getRegistryName()));
		proxy.registerItemRenderer(landia_portal_marker_item, 0, "landia_portal_marker");
		
		ItemBlock landia_tower_item = new ItemBlock(LandCraftContent.landia_tower) {
			@Override
			public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
				return LandCraftContent.landia_tower.testBlockPlacement(world, pos) ? super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ) : EnumActionResult.FAIL;
			}
		};
		event.getRegistry().register(landia_tower_item.setRegistryName(LandCraftContent.landia_tower.getRegistryName()));
		proxy.registerItemRenderer(landia_tower_item, 0, "landia_tower");
		
		event.getRegistry().register(LandCraftContent.redstone_component);
		proxy.registerItemRenderer(LandCraftContent.redstone_component, 0, "redstone_component");
		
		event.getRegistry().register(LandCraftContent.onion);
		OreDictionary.registerOre("cropOnion", LandCraftContent.onion);
		OreDictionary.registerOre("seedOnion", LandCraftContent.onion);
		proxy.registerItemRenderer(LandCraftContent.onion, 0, "onion");
		
		event.getRegistry().register(LandCraftContent.rice);
		OreDictionary.registerOre("cropRice", LandCraftContent.rice);
		OreDictionary.registerOre("seedRice", LandCraftContent.rice);
		proxy.registerItemRenderer(LandCraftContent.rice, 0, "rice");
		
		event.getRegistry().register(LandCraftContent.olive);
		OreDictionary.registerOre("cropOlive", LandCraftContent.olive);
		OreDictionary.registerOre("olive", LandCraftContent.olive);
		proxy.registerItemRenderer(LandCraftContent.olive, 0, "olive");
		
		event.getRegistry().register(LandCraftContent.potato_onion_pastry);
		proxy.registerItemRenderer(LandCraftContent.potato_onion_pastry, 0, "potato_onion_pastry");
		proxy.registerItemRenderer(LandCraftContent.potato_onion_pastry, 1, "potato_onion_pastry_cooked");
		
		event.getRegistry().register(LandCraftContent.pho);
		proxy.registerItemRenderer(LandCraftContent.pho, 0, "pho");
		proxy.registerItemRenderer(LandCraftContent.pho, 1, "pho_full");
		
		ItemBlockMeta landia_sapling_item = new ItemBlockMeta(LandCraftContent.landia_sapling);
		event.getRegistry().register(landia_sapling_item.setRegistryName(LandCraftContent.landia_sapling.getRegistryName()));
		for (LandiaTreeType type: LandiaTreeType.values()) {
			proxy.registerItemRenderer(landia_sapling_item, type.ordinal(), "landia_sapling_"+type);
			OreDictionary.registerOre("treeSapling", new ItemStack(LandCraftContent.landia_sapling, 1, type.ordinal()));
		}
		
		ItemBlockLeaves landia_leaves_item = new ItemBlockLeaves(LandCraftContent.landia_leaves);
		event.getRegistry().register(landia_leaves_item.setRegistryName(LandCraftContent.landia_leaves.getRegistryName()));
		for (LandiaTreeType type: LandiaTreeType.values()) {
			proxy.registerItemRenderer(landia_leaves_item, type.ordinal(), "landia_leaves",
					String.format(Locale.US, "%s=%s", LandiaTreeType.L_TYPE.getName(), type));
			OreDictionary.registerOre("treeLeaves", new ItemStack(LandCraftContent.landia_leaves, 1, type.ordinal()));
		}
		
		ItemBlockMeta landia_log_item = new ItemBlockMeta(LandCraftContent.landia_log);
		event.getRegistry().register(landia_log_item.setRegistryName(LandCraftContent.landia_log.getRegistryName()));
		for (LandiaTreeType type: LandiaTreeType.values()) {
			String variant = String.format(Locale.US, "%s=%s,%s=%s",
					Axis.AXIS.getName(), Axis.Y,
					LandiaTreeType.L_TYPE.getName(), type);
			proxy.registerItemRenderer(landia_log_item, type.ordinal(), "landia_log", variant);
			OreDictionary.registerOre("logWood", new ItemStack(LandCraftContent.landia_log, 1, type.ordinal()));
		}
		
		ItemBlockMeta landia_planks_item = new ItemBlockMeta(LandCraftContent.landia_planks);
		event.getRegistry().register(landia_planks_item.setRegistryName(LandCraftContent.landia_planks.getRegistryName()));
		for (LandiaTreeType type: LandiaTreeType.values()) {
			proxy.registerItemRenderer(landia_planks_item,
					type.ordinal(), "landia_planks",
					String.format(Locale.US, "%s=%s", LandiaTreeType.L_TYPE.getName(), type));
			OreDictionary.registerOre("plankWood", new ItemStack(LandCraftContent.landia_planks, 1, type.ordinal()));
		}
		
		event.getRegistry().register(LandCraftContent.cinnamon);
		proxy.registerItemRenderer(LandCraftContent.cinnamon, 0, "cinnamon");
		OreDictionary.registerOre("cropCinnamon", LandCraftContent.cinnamon);
		
		ItemBlockSlab<LandiaTreeType> landia_wood_slab_item = new ItemBlockSlab<>(LandCraftContent.landia_wood_slab);
		event.getRegistry().register(landia_wood_slab_item.setRegistryName(LandCraftContent.landia_wood_slab.getRegistryName()));
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
			Block block = LandCraftContent.landia_wood_stairs.get(type);
			ItemBlock ib = new ItemBlock(block);
			event.getRegistry().register(ib.setRegistryName(block.getRegistryName()));
			proxy.registerItemRenderer(ib, 0, block.getRegistryName().getResourcePath());
			OreDictionary.registerOre("stairWood", block);
		}
		
		if (Config.breeder) {
			ItemBlock breeder_item = new ItemBlock(LandCraftContent.breeder);
			event.getRegistry().register(breeder_item.setRegistryName(LandCraftContent.breeder.getRegistryName()));
			proxy.registerItemRenderer(breeder_item, 0, "breeder");
			GameRegistry.registerTileEntity(TEBreeder.class, ModInfo.MODID+"_breeder_reactor");
		}
		
		if (Config.player_mime) {
			ItemBlock player_mime_item = new ItemBlock(LandCraftContent.player_mime);
			event.getRegistry().register(player_mime_item.setRegistryName(LandCraftContent.player_mime.getRegistryName()));
			proxy.registerItemRenderer(player_mime_item, 0, "player_mime");
			GameRegistry.registerTileEntity(TEPlayerMime.class, ModInfo.MODID+"_player_mime");
		}
		
		if (Config.thorium_generator) {
			ItemBlock thorium_generator_item = new ItemBlock(LandCraftContent.thorium_generator);
			event.getRegistry().register(thorium_generator_item.setRegistryName(LandCraftContent.thorium_generator.getRegistryName()));
			proxy.registerItemRenderer(thorium_generator_item, 0, "thorium_generator");
			GameRegistry.registerTileEntity(TEThoriumGenerator.class, ModInfo.MODID+"_thorium_generator");
		}
		
		if (Config.pot) {
			ItemBlock pot_item = new ItemBlock(LandCraftContent.pot);
			event.getRegistry().register(pot_item.setRegistryName(LandCraftContent.pot.getRegistryName()));
			proxy.registerItemRenderer(pot_item, 0, "pot");
			GameRegistry.registerTileEntity(TEPot.class, ModInfo.MODID+"_pot");
		}
		
		if (Config.wrench) {
			event.getRegistry().register(LandCraftContent.wrench);
			proxy.registerItemRenderer(LandCraftContent.wrench, 0, "item_wrench");
		}
		
		final LandiaOreType[] oreValues = LandiaOreType.values();
		
		ItemBlock landia_ore_item = new ItemBlockMeta(LandCraftContent.landia_ore);
		event.getRegistry().register(landia_ore_item.setRegistryName(LandCraftContent.landia_ore.getRegistryName()));
		for (int i=0; i<oreValues.length; ++i) {
			proxy.registerItemRenderer(landia_ore_item, i, LandCraftContent.landia_ore.getRegistryName().getResourcePath(), "type="+oreValues[i]);
			OreDictionary.registerOre("ore"+StringUtils.capitalize(oreValues[i].toString()),
					new ItemStack(LandCraftContent.landia_ore, 1, i));
		}
		
		ItemBlock landia_metal_item = new ItemBlockMeta(LandCraftContent.landia_metal);
		event.getRegistry().register(landia_metal_item.setRegistryName(LandCraftContent.landia_metal.getRegistryName()));
		for (int i=0; i<oreValues.length; ++i) {
			proxy.registerItemRenderer(landia_metal_item, i, LandCraftContent.landia_metal.getRegistryName().getResourcePath(), "type="+oreValues[i]);
			OreDictionary.registerOre("block"+StringUtils.capitalize(oreValues[i].toString()),
					new ItemStack(LandCraftContent.landia_metal, 1, i));
		}
		
		event.getRegistry().register(LandCraftContent.landia_ingot);
		for (int i=0; i<oreValues.length; ++i) {
			proxy.registerItemRenderer(LandCraftContent.landia_ingot, i, "ingot/"+oreValues[i]);
			OreDictionary.registerOre("ingot"+StringUtils.capitalize(oreValues[i].toString()),
					new ItemStack(LandCraftContent.landia_ingot, 1, i));
		}
		
		for (LandiaOreType type: oreValues) {
			LandCraftContent.toolsMap.put(type, Utils.registerTools(event.getRegistry(), type,
					EnumHelper.addToolMaterial(
							type.name(), type.getLevel()+1, type.getDurability(),
							type.getEfficiency(), type.getDamage(), type.getEnchantability()),
					type.getAxeDamage(), type.getAxeSpeed()));
		}
		
		event.getRegistry().register(LandCraftContent.landia_crab_flesh);
		proxy.registerItemRenderer(LandCraftContent.landia_crab_flesh, 0, "landia_crab_flesh");
		OreDictionary.registerOre("foodCrab", LandCraftContent.landia_crab_flesh);
		OreDictionary.registerOre("foodCrabLandia", LandCraftContent.landia_crab_flesh);
		
		event.getRegistry().register(LandCraftContent.bun_rieu);
		proxy.registerItemRenderer(LandCraftContent.bun_rieu, 0, "bun_rieu");
		proxy.registerItemRenderer(LandCraftContent.bun_rieu, 1, "bun_rieu_full");
		
		event.getRegistry().registerAll(LandCraftContent.tomato_crop_item, LandCraftContent.tomato);
		proxy.registerItemRenderer(LandCraftContent.tomato_crop_item, 0, "tomato_crop");
		proxy.registerItemRenderer(LandCraftContent.tomato, 0, "tomato");
		OreDictionary.registerOre("cropTomato", LandCraftContent.tomato);
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		(config = new Config(event)).sync();
		LCLog.log = event.getModLog();
		
		proxy.initEntities();
		
		proxy.bindTESRs();
		
		LandiaWorldProvider.register();
		
		initWorldgen();
	}
	
	private static void initWorldgen() {
		GameRegistry.registerWorldGenerator(new LandiaOreWorldgen(), 10);
		
		GameRegistry.registerWorldGenerator(new CinnamonWorldgen(), 50);
		GameRegistry.registerWorldGenerator(new OliveWorldgen(), 51);
		
		GameRegistry.registerWorldGenerator(new OnionWorldgen(), 28);
		GameRegistry.registerWorldGenerator(new RiceWorldgen(), 33);
	}
	
	@SubscribeEvent
	public static void addRecipes(RegistryEvent.Register<IRecipe> event) {
		BreederFeedstock.addOreDict("ingotIron", 16, 200);
		BreederFeedstock.addOreDict("ingotTungsten", 64, 1000);
		BreederFeedstock.addOreDict("ingotLandium", 100, 1200);
		
		event.getRegistry().register(new ShapedOreRecipe(LandCraftContent.landia_tower.getRegistryName(),
				LandCraftContent.landia_tower,
				" K ", "GLM", "FLR", 'K', "blockKelline", 'G', "blockGarfax",
				'M', "blockMorganine", 'R', "blockRacheline", 'F', "blockFriscion",
				'L', "blockLandium")
				.setRegistryName(LandCraftContent.landia_tower.getRegistryName()));
		
		event.getRegistry().register(new ShapedOreRecipe(LandCraftContent.landia_portal_marker.getRegistryName(),
				LandCraftContent.landia_portal_marker,
				" d ", "lnl", "aea",
				'd', "gemDiamond", 'l', "ingotLandium",
				'n', Items.ENDER_PEARL, 'a', "ingotGold",
				'e', "gemEmerald")
				.setRegistryName(LandCraftContent.landia_portal_marker.getRegistryName()));
		
		event.getRegistry().register(new ShapedOreRecipe(LandCraftContent.redstone_component.getRegistryName(),
				LandCraftContent.redstone_component,
				"fdh",
				'f', "ingotIron", 'd', "dustRedstone",
				'h', "ingotThorium")
				.setRegistryName(LandCraftContent.redstone_component.getRegistryName()));
		
		final LandiaOreType[] values = LandiaOreType.values();
		for (int i=0; i<values.length; ++i) {
			final String ingotName = "ingot"+StringUtils.capitalize(values[i].toString());
			
			GameRegistry.addSmelting(new ItemStack(LandCraftContent.landia_ore, 1, i),
					new ItemStack(LandCraftContent.landia_ingot, 1, i), 1.25f);
			event.getRegistry().register(new ShapedOreRecipe(LandCraftContent.landia_metal.getRegistryName(),
					new ItemStack(LandCraftContent.landia_metal, 1, i),
					"III", "III", "III",
					'I', ingotName)
					.setRegistryName(Tools.underscoreSuffix(LandCraftContent.landia_metal.getRegistryName(), values[i])));
			event.getRegistry().register(new ShapelessRecipes(
					LandCraftContent.landia_ingot.getRegistryName().toString(),
					new ItemStack(LandCraftContent.landia_ingot, 9, i),
					NonNullList.from(Ingredient.EMPTY, Ingredient.fromStacks(new ItemStack(LandCraftContent.landia_metal, 1, i))))
					.setRegistryName(Tools.underscoreSuffix(LandCraftContent.landia_ingot.getRegistryName(), values[i])));
		}
		
		for (Utils.ToolGroup group: LandCraftContent.toolsMap.values()) {
			group.initRecipes(event.getRegistry());
		}
		
		event.getRegistry().register(new ShapelessOreRecipe(LandCraftContent.potato_onion_pastry.getRegistryName(),
				LandCraftContent.potato_onion_pastry,
				"cropPotato", "cropOnion", "cropWheat", "cropWheat")
				.setRegistryName(LandCraftContent.potato_onion_pastry.getRegistryName()));
		GameRegistry.addSmelting(new ItemStack(LandCraftContent.potato_onion_pastry),
				new ItemStack(LandCraftContent.potato_onion_pastry, 1, 1), 0.35f);
		
		PotRecipes.addRecipe(PotRecipes.RecipePOredict.of(
				"cropCinnamon", "cropOnion", "bone", new FluidStack(FluidRegistry.WATER, 1),
				new PotRecipes.RecipeOutput(new ItemStack(LandCraftContent.pho), 4, 5, 1200)));
		event.getRegistry().register(new ShapelessOreRecipe(LandCraftContent.pho.getRegistryName(),
				new ItemStack(LandCraftContent.pho,1,1),
				new ItemStack(LandCraftContent.pho), "cropRice", "cropRice", "cropRice", Items.BOWL, Items.BEEF)
				.setRegistryName(Tools.underscoreSuffix(LandCraftContent.pho.getRegistryName(), "full")));
		
		PotRecipes.addRecipe(PotRecipes.RecipePOredict.of("cropTomato", "foodCrabLandia", "cropOnion", new FluidStack(FluidRegistry.WATER, 1),
				new PotRecipes.RecipeOutput(new ItemStack(LandCraftContent.bun_rieu), 10, 100, 200)));
		event.getRegistry().register(new ShapelessOreRecipe(LandCraftContent.bun_rieu.getRegistryName(),
				new ItemStack(LandCraftContent.bun_rieu,1,1),
				new ItemStack(LandCraftContent.bun_rieu), "cropRice", "cropRice", "cropRice", Items.BOWL)
				.setRegistryName(Tools.underscoreSuffix(LandCraftContent.bun_rieu.getRegistryName(), "full")));
		
		
		for (final LandiaTreeType type: LandiaTreeType.values()) {
			final int i = type.ordinal();
			event.getRegistry().register(new ShapelessRecipes(LandCraftContent.landia_planks.getRegistryName().toString(),
					new ItemStack(LandCraftContent.landia_planks, 4, i),
					NonNullList.from(Ingredient.EMPTY, Ingredient.fromStacks(new ItemStack(LandCraftContent.landia_log, 1, i))))
					.setRegistryName(Tools.underscoreSuffix(LandCraftContent.landia_planks.getRegistryName(), type)));
			event.getRegistry().register(new ShapedRecipes(
					LandCraftContent.landia_wood_slab.getRegistryName().toString(),
					3, 1,
					Stream.of("ppp")
					.flatMapToInt(String::chars)
					.mapToObj(c -> c == 'p' ? new ItemStack[] { new ItemStack(LandCraftContent.landia_planks, 1, i) } : new ItemStack[0])
					.map(Ingredient::fromStacks)
					.collect(Collectors.toCollection(NonNullList::create)),
					new ItemStack(LandCraftContent.landia_wood_slab, 6, i))
					.setRegistryName(Tools.underscoreSuffix(LandCraftContent.landia_wood_slab.getRegistryName(), type)));
			
			final Block stairs = LandCraftContent.landia_wood_stairs.get(type);
			event.getRegistry().register(new ShapedRecipes(
					stairs.getRegistryName().toString(),
					3, 3,
					Stream.of("p  ", "pp ", "ppp")
					.flatMapToInt(String::chars)
					.mapToObj(c -> c == 'p' ? new ItemStack[] { new ItemStack(LandCraftContent.landia_planks, 1, i) } : new ItemStack[0])
					.map(Ingredient::fromStacks)
					.collect(Collectors.toCollection(NonNullList::create)),
					new ItemStack(stairs, 4))
					.setRegistryName(Tools.underscoreSuffix(stairs.getRegistryName(), type)));
			
			GameRegistry.addSmelting(new ItemStack(LandCraftContent.landia_log, 1, i), new ItemStack(Items.COAL, 1, 1), 0.15f);
		}
		
		if (Config.breeder) {
			event.getRegistry().register(new ShapedOreRecipe(LandCraftContent.breeder.getRegistryName(),
					LandCraftContent.breeder,
					"wdw", "wFw", "hlh",
					'w', "ingotTungsten", 'h', "ingotThorium",
					'd', "gemDiamond", 'F', Blocks.FURNACE,
					'l', "ingotLandium")
					.setRegistryName(LandCraftContent.breeder.getRegistryName()));
		}
		
		if (Config.player_mime) {
			event.getRegistry().register(new ShapedOreRecipe(LandCraftContent.player_mime.getRegistryName(),
					LandCraftContent.player_mime,
					"rSr", "hOh", "www",
					'S', Items.DIAMOND_SWORD, 'h', "ingotThorium",
					'O', Blocks.OBSIDIAN, 'w', "ingotTungsten",
					'r', LandCraftContent.redstone_component)
					.setRegistryName(LandCraftContent.player_mime.getRegistryName()));
		}
		
		if (Config.thorium_generator) {
			event.getRegistry().register(new ShapedOreRecipe(LandCraftContent.thorium_generator.getRegistryName(),
					LandCraftContent.thorium_generator,
					"rzr", "wFw", "ara",
					'w', "ingotTungsten", 'r', LandCraftContent.redstone_component,
					'F', Blocks.FURNACE, 'z', "gemLapis", 'a', "ingotGold")
					.setRegistryName(LandCraftContent.thorium_generator.getRegistryName()));
		}
		
		if (Config.pot) {
			event.getRegistry().register(new ShapedOreRecipe(LandCraftContent.pot.getRegistryName(),
					LandCraftContent.pot,
					"m m", "lFl", "lll",
					'm', "ingotMorganine", 'l', "ingotLandium",
					'F', Blocks.FURNACE)
					.setRegistryName(LandCraftContent.pot.getRegistryName()));
		}
		
		if (Config.wrench) {
			event.getRegistry().register(new ShapedOreRecipe(LandCraftContent.wrench.getRegistryName(),
					LandCraftContent.wrench,
					"w", "f", "z",
					'w', "ingotTungsten", 'f', "ingotIron",
					'z', "gemLapis")
					.setRegistryName(LandCraftContent.wrench.getRegistryName()));
		}
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.initColorHandlers();
		
		NetworkRegistry.INSTANCE.registerGuiHandler(INSTANCE, new GuiProxy());
	}
}
