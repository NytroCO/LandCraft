package landmaster.landcraft.util;

import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.commons.lang3.*;

import com.google.common.base.*;
import com.google.common.collect.*;

import landmaster.landcraft.*;
import landmaster.landcraft.item.*;
import net.minecraft.client.*;
import net.minecraft.client.resources.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.registry.*;
import net.minecraftforge.fml.relauncher.*;
import net.minecraftforge.oredict.*;

public class Utils {
	private static final MethodHandle regionF;
	static {
		try {
			Field temp = Language.class.getDeclaredField("field_135037_b" /* region */);
			temp.setAccessible(true);
			regionF = MethodHandles.lookup().unreflectGetter(temp);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	@SideOnly(Side.CLIENT)
	public static java.util.Locale getLocale() {
		try {
			final Language lang = Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage();
			return new java.util.Locale(lang.getLanguageCode(), (String)regionF.invokeExact(lang));
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	private static final MethodHandle getOresM;
	static {
		try {
			Method temp = OreDictionary.class.getDeclaredMethod("getOres", int.class);
			temp.setAccessible(true);
			getOresM = MethodHandles.lookup().unreflect(temp);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	public static List<ItemStack> getOres(int id) {
		try {
			return (List<ItemStack>)getOresM.invoke(id);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	public static AxisAlignedBB AABBfromVecs(Vec3d v1, Vec3d v2) {
		return new AxisAlignedBB(v1.xCoord, v1.yCoord, v1.zCoord, v2.xCoord, v2.yCoord, v2.zCoord);
	}
	
	public static int getFluidAmount(FluidStack fs) {
		return fs != null ? fs.amount : 0;
	}
	
	public static <T extends TileEntity> List<T> getTileEntitiesWithinAABB(World world, Class<? extends T> tileEntityClass, AxisAlignedBB aabb) {
		int i = MathHelper.floor((aabb.minX - World.MAX_ENTITY_RADIUS) / 16.0D);
		int j = MathHelper.floor((aabb.maxX + World.MAX_ENTITY_RADIUS) / 16.0D);
		int k = MathHelper.floor((aabb.minZ - World.MAX_ENTITY_RADIUS) / 16.0D);
		int l = MathHelper.floor((aabb.maxZ + World.MAX_ENTITY_RADIUS) / 16.0D);
		ArrayList<T> arraylist = Lists.newArrayList();

		for (int i1 = i; i1 <= j; ++i1) {
			for (int j1 = k; j1 <= l; ++j1) {
				if (isChunkLoaded(world, i1, j1, true)) {
					Iterator<TileEntity> iterator = world.getChunkFromChunkCoords(i1, j1).getTileEntityMap().values().iterator();
					while (iterator.hasNext()) {
						TileEntity te = iterator.next();
						if(tileEntityClass.isInstance(te)) {
							if (world.getBlockState(te.getPos()).getBoundingBox(world, te.getPos())
									.offset(te.getPos()).intersectsWith(aabb)) {
								arraylist.add(tileEntityClass.cast(te));
			                }
						}
					}
				}
			}
		}
		return arraylist;
	}
	
	public static enum ToolType {
		SWORD, PICKAXE, AXE, SHOVEL, HOE;
		
		@Override
		public String toString() {
			return name().toLowerCase(java.util.Locale.US);
		}
	}
	
	public static class ToolGroup {
		public final ImmutableList<Item> tools;
		public final LandiaOreType metal;
		
		ToolGroup(LandiaOreType metal, Item...items) {
			Preconditions.checkNotNull(metal);
			this.metal = metal;
			
			Preconditions.checkNotNull(items);
			Preconditions.checkArgument(items.length == ToolType.values().length,
					String.format("There should be %d items passed in but got %d instead",
							ToolType.values().length, items.length));
			
			this.tools = ImmutableList.copyOf(items);
		}
		
		public Item getTool(ToolType type) {
			return tools.get(type.ordinal());
		}
		
		public void initRecipes() {
			final String ingotName = "ingot" + StringUtils.capitalize(metal.toString());
			
			GameRegistry.addRecipe(new ShapedOreRecipe(getTool(ToolType.SWORD),
					"I", "I", "S",
					'I', ingotName, 'S', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(getTool(ToolType.PICKAXE),
					"III", " S ", " S ",
					'I', ingotName, 'S', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(getTool(ToolType.AXE),
					"II", "IS", " S",
					'I', ingotName, 'S', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(getTool(ToolType.SHOVEL),
					"I", "S", "S",
					'I', ingotName, 'S', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(getTool(ToolType.HOE),
					"II", " S", " S",
					'I', ingotName, 'S', "stickWood"));
		}
	}
	
	public static ToolGroup registerTools(LandiaOreType metal, Item.ToolMaterial material, float axeDamage, float axeSpeed) {
		Item[] items = new Item[] {
				new ItemModSword(material, metal + "_" + ToolType.SWORD),
				new ItemModPickaxe(material, metal + "_" + ToolType.PICKAXE),
				new ItemModAxe(material, axeDamage, axeSpeed, metal + "_" + ToolType.AXE),
				new ItemModShovel(material, metal + "_" + ToolType.SHOVEL),
				new ItemModHoe(material, metal + "_" + ToolType.HOE)
		};
		
		final ToolType[] values = ToolType.values();
		
		for (int i=0; i<items.length; ++i) {
			GameRegistry.register(items[i]);
			LandCraft.proxy.registerItemRenderer(items[i], 0, "tool/" + metal + "_" + values[i]);
		}
		
		return new ToolGroup(metal, items);
	}
	
	private static final MethodHandle isChunkLoadedM;
	static {
		try {
			Method temp = World.class.getDeclaredMethod("func_175680_a"/*isChunkLoaded*/,
					int.class, int.class, boolean.class);
			temp.setAccessible(true);
			isChunkLoadedM = MethodHandles.lookup().unreflect(temp);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	private static boolean isChunkLoaded(World world, int x, int z, boolean allowEmpty) {
		try {
			return (boolean)isChunkLoadedM.invoke(world, x, z, allowEmpty);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
}
