package landmaster.landcore.api;

import java.lang.reflect.*;
import java.util.*;

import com.google.common.base.*;

import mcjty.lib.tools.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.play.server.*;
import net.minecraft.potion.*;
import net.minecraft.server.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.*;

public class Tools {
	private static final Method getBiomesImplM;
	static {
		try {
			Method temp;
			try {
				temp = BiomeDictionary.class.getMethod("getBiomes", BiomeDictionary.Type.class);
			} catch (NoSuchMethodException error) {
				temp = BiomeDictionary.class.getMethod("getBiomesForType", BiomeDictionary.Type.class);
			}
			getBiomesImplM = temp;
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Biome[] getBiomesForType(BiomeDictionary.Type type) {
		try {
			Object obj = getBiomesImplM.invoke(null, type);
			if (obj instanceof Set) {
				return ((Set<Biome>)getBiomesImplM.invoke(null, type)).toArray(new Biome[0]);
			}
			return (Biome[])obj;
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	private static final Method getCollisionBoundingBoxM;
	
	static {
		try {
			Method temp;
			try {
				temp = IBlockState.class.getMethod("func_185890_d", IBlockAccess.class, BlockPos.class);
			} catch (NoSuchMethodException e) {
				temp = IBlockState.class.getMethod("func_185890_d", World.class, BlockPos.class);
			}
			getCollisionBoundingBoxM = temp;
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	public static boolean canTeleportTo(EntityPlayer player, Coord4D dest) {
		try {
			if (dest == null || dest.world() == null) return false;
			for (int i=1; i<=2; ++i) {
				if (getCollisionBoundingBoxM.invoke(dest.add(0, i, 0).blockState(), dest.world(), dest.pos()) != null) {
					return false;
				}
			}
			return true;
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	public static NBTTagCompound getTagSafe(ItemStack is, boolean set) {
		if (ItemStackTools.isEmpty(is)) {
			return new NBTTagCompound();
		}
		NBTTagCompound nbt = is.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
			if (set) is.setTagCompound(nbt);
		}
		return nbt;
	}
	
	public static void teleportPlayerTo(EntityPlayerMP player, Coord4D coord) {
		if (player.dimension != coord.dimensionId) {
			int id = player.dimension;
			WorldServer oldWorld = player.mcServer.worldServerForDimension(player.dimension);
			player.dimension = coord.dimensionId;
			WorldServer newWorld = player.mcServer.worldServerForDimension(player.dimension);
			player.connection.sendPacket(new SPacketRespawn(player.dimension, player.worldObj.getDifficulty(), newWorld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
			oldWorld.removeEntityDangerously(player);
			player.isDead = false;
			
			if(player.isEntityAlive())
			{
				newWorld.spawnEntityInWorld(player);
				player.setLocationAndAngles(coord.xCoord+0.5, coord.yCoord+1, coord.zCoord+0.5, player.rotationYaw, player.rotationPitch);
				newWorld.updateEntityWithOptionalForce(player, false);
				player.setWorld(newWorld);
			}
	
			player.mcServer.getPlayerList().preparePlayer(player, oldWorld);
			player.connection.setPlayerLocation(coord.xCoord+0.5, coord.yCoord+1, coord.zCoord+0.5, player.rotationYaw, player.rotationPitch);
			player.interactionManager.setWorld(newWorld);
			player.mcServer.getPlayerList().updateTimeAndWeatherForPlayer(player, newWorld);
			player.mcServer.getPlayerList().syncPlayerInventory(player);
	
			for(PotionEffect potioneffect : player.getActivePotionEffects())
			{
				player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
			}
	
			player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel)); // Force XP sync
	
			FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, id, coord.dimensionId);
		}
		else {
			player.connection.setPlayerLocation(coord.xCoord+0.5, coord.yCoord+1, coord.zCoord+0.5, player.rotationYaw, player.rotationPitch);
		}
	}
	
	public static void teleportEntityTo(Entity entity, Coord4D coord) {
		final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		WorldServer world = server.worldServerForDimension(coord.dimensionId);

		if (entity.worldObj.provider.getDimension() != coord.dimensionId) {
			synchronized (entity) {
				entity.worldObj.removeEntity(entity);
				entity.isDead = false;
				entity.setWorld(world);
				world.updateEntityWithOptionalForce(entity, false);
				world.resetUpdateEntityTick();
				AxisAlignedBB bb = entity.getEntityBoundingBox();
				entity.setLocationAndAngles(coord.xCoord+(bb.maxX-bb.minX)*0.5, coord.yCoord+(bb.maxY-bb.minY)*0.5, coord.zCoord+(bb.maxZ-bb.minZ)*0.5, entity.rotationYaw, entity.rotationPitch);
				world.spawnEntityInWorld(entity);
			}
		}
		else {
			entity.setLocationAndAngles(coord.xCoord+0.5, coord.yCoord+1, coord.zCoord+0.5, entity.rotationYaw, entity.rotationPitch);
		}
	}
}
