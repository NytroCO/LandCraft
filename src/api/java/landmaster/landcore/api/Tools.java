package landmaster.landcore.api;

import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.Random;

public class Tools {
	private static ResourceLocation suffix(ResourceLocation original, Object suffix) {
		return new ResourceLocation(original.getResourceDomain(), original.getResourcePath()+suffix);
	}
	
	public static ResourceLocation underscoreSuffix(ResourceLocation original, Object suffix) {
		return suffix(original, "_"+suffix);
	}
	
	public static boolean canTeleportTo(EntityPlayer player, Coord4D dest) {
		if (dest == null || dest.world() == null) return false;
		for (int i=1; i<=2; ++i) {
			if (dest.add(0, i, 0).blockState().getCollisionBoundingBox(dest.world(), dest.pos()) != null) {
				return false;
			}
		}
		return true;
	}
	
	public static NBTTagCompound getTagSafe(ItemStack is, boolean set) {
		if (is.isEmpty()) {
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
			WorldServer oldWorld = player.mcServer.getWorld(player.dimension);
			player.dimension = coord.dimensionId;
			WorldServer newWorld = player.mcServer.getWorld(player.dimension);
			player.connection.sendPacket(new SPacketRespawn(player.dimension, player.world.getDifficulty(), newWorld.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
			oldWorld.removeEntityDangerously(player);
			player.isDead = false;
			
			if(player.isEntityAlive())
			{
				newWorld.spawnEntity(player);
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
		WorldServer world = server.getWorld(coord.dimensionId);

		if (entity.world.provider.getDimension() != coord.dimensionId) {
			entity.world.removeEntity(entity);
			entity.isDead = false;
			entity.setWorld(world);
			world.updateEntityWithOptionalForce(entity, false);
			world.resetUpdateEntityTick();
			entity.setLocationAndAngles(coord.xCoord+0.5, coord.yCoord+1, coord.zCoord+0.5, entity.rotationYaw, entity.rotationPitch);
			world.spawnEntity(entity);
		}
		else {
			entity.setLocationAndAngles(coord.xCoord+0.5, coord.yCoord+1, coord.zCoord+0.5, entity.rotationYaw, entity.rotationPitch);
		}
	}

	public static void generateOre(IBlockState ore, World world, Random random, int x, int z, int minY, int maxY, int minSize, int maxSize, int chances) {
		Tools.generateOre(ore, world, BlockMatcher.forBlock(Blocks.STONE), random, x, z, minY, maxY, minSize, maxSize, chances);
	}

	private static void generateOre(IBlockState ore, World world, Predicate<IBlockState> matcher, Random random, int x, int z, int minY, int maxY, int minSize, int maxSize, int chances) {
		int deltaY = maxY - minY;
		int deltaSize = maxSize - minSize;
		for (int i = 0; i < chances; i++) {
			BlockPos pos = new BlockPos(x + random.nextInt(16), minY + random.nextInt(deltaY), z + random.nextInt(16));
			WorldGenMinable generator = new WorldGenMinable(ore, minSize + random.nextInt(deltaSize+1), matcher);
			generator.generate(world, random, pos);
		}
	}
}
