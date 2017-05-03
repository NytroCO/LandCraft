package landmaster.landcraft.util;

import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.*;

import com.google.common.base.*;
import com.google.common.collect.*;

import net.minecraft.tileentity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class Utils {
	public static AxisAlignedBB AABBfromVecs(Vec3d v1, Vec3d v2) {
		return new AxisAlignedBB(v1.xCoord, v1.yCoord, v1.zCoord, v2.xCoord, v2.yCoord, v2.zCoord);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends TileEntity> List<T> getTileEntitiesWithinAABB(World world, Class<? extends T> tileEntityClass, AxisAlignedBB aabb)
	{
		int i = MathHelper.floor_double((aabb.minX - World.MAX_ENTITY_RADIUS) / 16.0D);
		int j = MathHelper.floor_double((aabb.maxX + World.MAX_ENTITY_RADIUS) / 16.0D);
		int k = MathHelper.floor_double((aabb.minZ - World.MAX_ENTITY_RADIUS) / 16.0D);
		int l = MathHelper.floor_double((aabb.maxZ + World.MAX_ENTITY_RADIUS) / 16.0D);
		ArrayList<T> arraylist = Lists.newArrayList();

		for (int i1 = i; i1 <= j; ++i1)
		{
			for (int j1 = k; j1 <= l; ++j1)
			{
				if(isChunkLoaded(world, i1, j1, true))
				{
					Iterator<TileEntity> iterator = world.getChunkFromChunkCoords(i1, j1).getTileEntityMap().values().iterator();

					while (iterator.hasNext())
					{
						TileEntity te = iterator.next();
						if(tileEntityClass.isInstance(te))
						{
							if (world.getBlockState(te.getPos()).getBoundingBox(world, te.getPos()).offset(te.getPos()).intersectsWith(aabb))
			                {
								arraylist.add((T)te);
			                }
						}
					}
				}
			}
		}
		return arraylist;
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
