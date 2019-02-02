package landmaster.landcraft.block;

import java.util.*;

import javax.annotation.*;

import landmaster.landcraft.content.*;
import landmaster.landcraft.tile.*;
import landmaster.landcraft.util.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.state.*;
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
		if (event.getEntityLiving().recentlyHit < 22
				|| event.getEntityLiving().attackingPlayer == null) {
			for (TEPlayerMime te: tiles) {
				if (te.isEnabled(te)
						&& te.extractEnergy(null, 1000, true) >= 1000
						&& !event.getEntity().getEntityWorld().isBlockPowered(te.getPos())) {
					te.extractEnergy(null, 1000, false);
					event.getEntityLiving().recentlyHit = 100;
					event.getEntityLiving().attackingPlayer = FakePlayerFactory.getMinecraft((WorldServer)event.getEntity().getEntityWorld());
				}
			}
		}
	}
}
