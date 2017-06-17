package landmaster.landcraft.util;

import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.*;

import org.apache.commons.lang3.*;

import com.google.common.base.*;
import com.google.common.collect.*;

import landmaster.landcraft.*;
import landmaster.landcraft.item.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.registry.*;
import net.minecraftforge.oredict.*;

public class Utils {
	// thanks Tinkers Construct
	public static RayTraceResult raytraceEntityPlayerLook(EntityLivingBase player, float range) {
		Vec3d eye = new Vec3d(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ); // Entity.getPositionEyes
		Vec3d look = player.getLook(1.0f);
		
		return raytraceEntity(player, eye, look, range, true);
	}
	
	// based on EntityRenderer.getMouseOver
	public static RayTraceResult raytraceEntity(Entity entity, Vec3d start, Vec3d look, double range,
			boolean ignoreCanBeCollidedWith) {
		// Vec3 look = entity.getLook(partialTicks);
		Vec3d direction = start.addVector(look.x * range, look.y * range, look.z * range);
		
		// Vec3 direction = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0,
		// vec31.zCoord * d0);
		Entity pointedEntity = null;
		Vec3d hit = null;
		AxisAlignedBB bb = entity.getEntityBoundingBox()
				.expand(look.x * range, look.y * range, look.z * range).grow(1, 1, 1);
		
		List<Entity> entitiesInArea = entity.getEntityWorld().getEntitiesWithinAABBExcludingEntity(entity, bb);
		double range2 = range; // range to the current candidate. Used to find
								// the closest entity.
		
		for (Entity candidate : entitiesInArea) {
			if (ignoreCanBeCollidedWith || candidate.canBeCollidedWith()) {
				// does our vector go through the entity?
				double colBorder = candidate.getCollisionBorderSize();
				AxisAlignedBB entityBB = candidate.getEntityBoundingBox().grow(colBorder, colBorder, colBorder);
				RayTraceResult movingobjectposition = entityBB.calculateIntercept(start, direction);
				
				// needs special casing: vector starts inside the entity
				if (entityBB.contains(start)) {
					if (0.0D < range2 || range2 == 0.0D) {
						pointedEntity = candidate;
						hit = movingobjectposition == null ? start : movingobjectposition.hitVec;
						range2 = 0.0D;
					}
				} else if (movingobjectposition != null) {
					double dist = start.distanceTo(movingobjectposition.hitVec);
					
					if (dist < range2 || range2 == 0.0D) {
						if (candidate == entity.getRidingEntity() && !entity.canRiderInteract()) {
							if (range2 == 0.0D) {
								pointedEntity = candidate;
								hit = movingobjectposition.hitVec;
							}
						} else {
							pointedEntity = candidate;
							hit = movingobjectposition.hitVec;
							range2 = dist;
						}
					}
				}
			}
		}
		
		if (pointedEntity != null && range2 < range) {
			return new RayTraceResult(pointedEntity, hit);
		}
		return null;
	}
	
	public static boolean matchesOre(ItemStack is, String od) {
		return OreDictionary.doesOreNameExist(od) && !is.isEmpty()
				&& ArrayUtils.contains(OreDictionary.getOreIDs(is), OreDictionary.getOreID(od));
	}
	
	private static final MethodHandle getOresM;
	static {
		try {
			Method temp = OreDictionary.class.getDeclaredMethod("getOres", int.class);
			temp.setAccessible(true);
			getOresM = MethodHandles.lookup().unreflect(temp);
		} catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}
	
	public static List<ItemStack> getOres(int id) {
		try {
			return (List<ItemStack>) getOresM.invoke(id);
		} catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}
	
	public static AxisAlignedBB AABBfromVecs(Vec3d v1, Vec3d v2) {
		return new AxisAlignedBB(v1.x, v1.y, v1.z, v2.x, v2.y, v2.z);
	}
	
	public static int getFluidAmount(FluidStack fs) {
		return fs != null ? fs.amount : 0;
	}
	
	public static <T extends TileEntity> List<T> getTileEntitiesWithinAABB(World world,
			Class<? extends T> tileEntityClass, AxisAlignedBB aabb) {
		int i = MathHelper.floor((aabb.minX - World.MAX_ENTITY_RADIUS) / 16.0D);
		int j = MathHelper.floor((aabb.maxX + World.MAX_ENTITY_RADIUS) / 16.0D);
		int k = MathHelper.floor((aabb.minZ - World.MAX_ENTITY_RADIUS) / 16.0D);
		int l = MathHelper.floor((aabb.maxZ + World.MAX_ENTITY_RADIUS) / 16.0D);
		ArrayList<T> arraylist = Lists.newArrayList();
		
		for (int i1 = i; i1 <= j; ++i1) {
			for (int j1 = k; j1 <= l; ++j1) {
				if (isChunkLoaded(world, i1, j1, true)) {
					Iterator<TileEntity> iterator = world.getChunkFromChunkCoords(i1, j1).getTileEntityMap().values()
							.iterator();
					while (iterator.hasNext()) {
						TileEntity te = iterator.next();
						if (tileEntityClass.isInstance(te)) {
							if (world.getBlockState(te.getPos()).getBoundingBox(world, te.getPos()).offset(te.getPos())
									.intersects(aabb)) {
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
		
		ToolGroup(LandiaOreType metal, Item... items) {
			Preconditions.checkNotNull(metal);
			this.metal = metal;
			
			Preconditions.checkNotNull(items);
			Preconditions.checkArgument(items.length == ToolType.values().length, String.format(
					"There should be %d items passed in but got %d instead", ToolType.values().length, items.length));
			
			this.tools = ImmutableList.copyOf(items);
		}
		
		public Item getTool(ToolType type) {
			return tools.get(type.ordinal());
		}
		
		public void initRecipes() {
			final String ingotName = "ingot" + StringUtils.capitalize(metal.toString());
			
			GameRegistry.register(
					new ShapedOreRecipe(getTool(ToolType.SWORD).getRegistryName(),
							getTool(ToolType.SWORD), "I", "I", "S", 'I', ingotName, 'S', "stickWood"));
			GameRegistry.register(
					new ShapedOreRecipe(getTool(ToolType.PICKAXE).getRegistryName(),
							getTool(ToolType.PICKAXE), "III", " S ", " S ", 'I', ingotName, 'S', "stickWood"));
			GameRegistry.register(
					new ShapedOreRecipe(getTool(ToolType.AXE).getRegistryName(),
							getTool(ToolType.AXE), "II", "IS", " S", 'I', ingotName, 'S', "stickWood"));
			GameRegistry.register(
					new ShapedOreRecipe(getTool(ToolType.SHOVEL).getRegistryName(),
							getTool(ToolType.SHOVEL), "I", "S", "S", 'I', ingotName, 'S', "stickWood"));
			GameRegistry.register(
					new ShapedOreRecipe(getTool(ToolType.HOE).getRegistryName(),
							getTool(ToolType.HOE), "II", " S", " S", 'I', ingotName, 'S', "stickWood"));
		}
	}
	
	public static ToolGroup registerTools(LandiaOreType metal, Item.ToolMaterial material, float axeDamage,
			float axeSpeed) {
		Item[] items = new Item[] { new ItemModSword(material, metal + "_" + ToolType.SWORD),
				new ItemModPickaxe(material, metal + "_" + ToolType.PICKAXE),
				new ItemModAxe(material, axeDamage, axeSpeed, metal + "_" + ToolType.AXE),
				new ItemModShovel(material, metal + "_" + ToolType.SHOVEL),
				new ItemModHoe(material, metal + "_" + ToolType.HOE) };
		
		final ToolType[] values = ToolType.values();
		
		for (int i = 0; i < items.length; ++i) {
			GameRegistry.register(items[i]);
			LandCraft.proxy.registerItemRenderer(items[i], 0, "tool/" + metal + "_" + values[i]);
		}
		
		return new ToolGroup(metal, items);
	}
	
	private static final MethodHandle isChunkLoadedM;
	static {
		try {
			final Method temp = World.class.getDeclaredMethod("func_175680_a", int.class, int.class, boolean.class);
			temp.setAccessible(true);
			isChunkLoadedM = MethodHandles.lookup().unreflect(temp);
		} catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}
	
	public static boolean isChunkLoaded(World world, int x, int z, boolean allowEmpty) {
		try {
			return (boolean)isChunkLoadedM.invokeExact(world, x, z, allowEmpty);
		} catch (Throwable e) {
			Throwables.throwIfUnchecked(e);
			throw new RuntimeException(e);
		}
	}
}
