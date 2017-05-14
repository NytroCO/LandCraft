package landmaster.landcraft.block;

import java.lang.invoke.*;
import java.lang.reflect.*;
import java.util.*;

import javax.annotation.*;

import com.google.common.base.*;

import landmaster.landcraft.content.*;
import landmaster.landcraft.tile.*;
import landmaster.landcraft.util.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.common.util.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class BlockPlayerMime extends BlockMachineBase {
	static {
		MinecraftForge.EVENT_BUS.register(BlockPlayerMime.class);
	}
	
	public BlockPlayerMime() {
		super(Material.ROCK);
		this.setHarvestLevel("pickaxe", 0);
		this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setUnlocalizedName("player_mime").setRegistryName("player_mime");
        this.setCreativeTab(LandCraftContent.creativeTab);
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TEPlayerMime createTileEntity(World world, IBlockState state) {
		return new TEPlayerMime();
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void death(LivingDeathEvent event) {
		if (event.getEntity().getEntityWorld().isRemote) return;
		
		List<TEPlayerMime> tiles = Utils.getTileEntitiesWithinAABB(
				event.getEntity().getEntityWorld(), TEPlayerMime.class,
				Utils.AABBfromVecs(event.getEntity().getPositionVector().subtract(2, 2, 2),
						event.getEntity().getPositionVector().addVector(2, 2, 2)));
		if (recentlyHit(event.getEntityLiving()) < 22
				|| attackingPlayer(event.getEntityLiving()) == null) {
			for (TEPlayerMime te: tiles) {
				if (te.isEnabled(te)
						&& te.extractEnergy(null, 1000, true) >= 1000
						&& !event.getEntity().getEntityWorld().isBlockPowered(te.getPos())) {
					te.extractEnergy(null, 1000, false);
					recentlyHit(event.getEntityLiving(), 100);
					FakePlayer fake = FakePlayerFactory.getMinecraft((WorldServer)event.getEntity().getEntityWorld());
					attackingPlayer(event.getEntityLiving(), fake);
				}
			}
		}
	}
	
	private static final MethodHandle recentlyHitGF;
	private static final MethodHandle recentlyHitSF;
	private static final MethodHandle attackingPlayerGF;
	private static final MethodHandle attackingPlayerSF;
	static {
		try {
			Field temp;
			temp = EntityLivingBase.class.getDeclaredField(
					"field_70718_bc"/*recentlyHit*/);
			temp.setAccessible(true);
			recentlyHitGF = MethodHandles.lookup().unreflectGetter(temp);
			recentlyHitSF = MethodHandles.lookup().unreflectSetter(temp);
			
			temp = EntityLivingBase.class.getDeclaredField(
					"field_70717_bb"/*attackingPlayer*/);
			temp.setAccessible(true);
			attackingPlayerGF = MethodHandles.lookup().unreflectGetter(temp);
			attackingPlayerSF = MethodHandles.lookup().unreflectSetter(temp);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	private static void recentlyHit(EntityLivingBase elb, int val) {
		try {
			recentlyHitSF.invoke(elb, val);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	private static int recentlyHit(EntityLivingBase elb) {
		try {
			return (int)recentlyHitGF.invoke(elb);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	private static void attackingPlayer(EntityLivingBase elb, EntityPlayer ep) {
		try {
			attackingPlayerSF.invoke(elb, ep);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
	
	private static EntityPlayer attackingPlayer(EntityLivingBase elb) {
		try {
			return (EntityPlayer)attackingPlayerGF.invoke(elb);
		} catch (Throwable e) {
			throw Throwables.propagate(e);
		}
	}
}
