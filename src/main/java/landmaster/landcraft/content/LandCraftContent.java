package landmaster.landcraft.content;

import java.util.*;

import landmaster.landcraft.api.ModInfo;
import landmaster.landcraft.block.*;
import landmaster.landcraft.item.*;
import landmaster.landcraft.util.*;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

public class LandCraftContent {

	public static final CreativeTabs creativeTab = new CreativeTabs(ModInfo.MODID) {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(LandCraftContent.redstone_component);
		}
	};
	
	public static final BlockLandiaTower landia_tower = new BlockLandiaTower();
	
	// MACHINES
	public static final BlockBreeder breeder = new BlockBreeder();
	public static final BlockPlayerMime player_mime = new BlockPlayerMime();
	public static final BlockThoriumGenerator thorium_generator = new BlockThoriumGenerator();
	public static final BlockPot pot = new BlockPot();
	
	// PORTAL
	public static final BlockLandiaPortalMarker landia_portal_marker = new BlockLandiaPortalMarker();
	
	// NATURE
	public static final BlockLandiaSapling landia_sapling = new BlockLandiaSapling();
	public static final BlockLandiaLog landia_log = new BlockLandiaLog();
	public static final BlockLandiaLeaves landia_leaves = new BlockLandiaLeaves();
	public static final BlockLandiaPlanks landia_planks = new BlockLandiaPlanks();
	public static final BlockCinnamonBark cinnamon_bark = new BlockCinnamonBark();
	public static final BlockLandiaWoodSlab landia_wood_slab = new BlockLandiaWoodSlab();
	public static final EnumMap<LandiaTreeType, BlockModStairs> landia_wood_stairs = new EnumMap<>(LandiaTreeType.class);
	public static final ItemCinnamon cinnamon = new ItemCinnamon();
	
	// AGRICULTURE
	public static final BlockWildOnion wild_onion = new BlockWildOnion();
	public static final BlockOnionCrop onion_crop = new BlockOnionCrop();
	public static final ItemOnion onion = new ItemOnion();
	public static final ItemPotatoOnionPastry potato_onion_pastry = new ItemPotatoOnionPastry();
	public static final BlockWildRice wild_rice = new BlockWildRice();
	public static final BlockRiceCrop rice_crop = new BlockRiceCrop();
	public static final ItemRice rice = new ItemRice();
	public static final ItemPho pho = new ItemPho();
	public static final ItemOlive olive = new ItemOlive();
	public static final ItemLandiaCrabFlesh landia_crab_flesh = new ItemLandiaCrabFlesh();
	public static final ItemBunRieu bun_rieu = new ItemBunRieu();
	public static final BlockTomatoCrop tomato_crop = new BlockTomatoCrop();
	public static final ItemBlockSpecial tomato_crop_item = (ItemBlockSpecial)new ItemBlockSpecial(tomato_crop) {
		@SideOnly(Side.CLIENT)
		@Override
		public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
			tooltip.add(I18n.format(""));
		}
	}
	.setUnlocalizedName("tomato_crop")
	.setRegistryName("tomato_crop")
	.setCreativeTab(creativeTab);
	public static final ItemTomato tomato = new ItemTomato();
	
	// TECH ITEMS
	public static final ItemWrench wrench = new ItemWrench();
	public static final Item redstone_component = new Item()
	.setUnlocalizedName("redstone_component")
	.setRegistryName("redstone_component")
	.setCreativeTab(creativeTab);
	public static final ItemLandmastersWings landmasters_wings = new ItemLandmastersWings();
	public static final ItemWeatherWand weather_wand = new ItemWeatherWand();
	
	// METALS
	public static final BlockLandiaOre landia_ore = new BlockLandiaOre();
	public static final BlockLandiaMetal landia_metal = new BlockLandiaMetal();
	public static final ItemLandiaIngot landia_ingot = new ItemLandiaIngot();
	public static final EnumMap<LandiaOreType, Utils.ToolGroup> toolsMap = new EnumMap<>(LandiaOreType.class);
	
}
