package landmaster.landcraft.block;

import java.util.*;

import javax.annotation.*;

import landmaster.landcraft.content.*;
import landmaster.landcraft.tile.*;
import landmaster.landcraft.util.*;
import mcjty.lib.compat.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.client.resources.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.relauncher.*;

public class BlockLandiaPortalMarker extends CompatBlock {
	public static final AxisAlignedBB LANDIA_PORTAL_MARKER_AABB = new AxisAlignedBB(0.0625D, 0.0D, 0.0625D, 0.9375D, 1.0D, 0.9375D);
	
	public static final PropertyBool ACTIVATED = PropertyBool.create("activated");
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void death(LivingDeathEvent event) {
		if (event.getEntity().getEntityWorld().isRemote
				|| event.getEntityLiving().getCreatureAttribute() != EnumCreatureAttribute.UNDEAD) return;
		
		List<TELandiaPortalMarker> tiles = Utils.getTileEntitiesWithinAABB(
				event.getEntity().getEntityWorld(), TELandiaPortalMarker.class,
				Utils.AABBfromVecs(event.getEntity().getPositionVector().subtract(4, 4, 4),
						event.getEntity().getPositionVector().addVector(4, 6, 4)));
		
		for (TELandiaPortalMarker tile: tiles) {
			tile.getWorld().setBlockState(tile.getPos(),
					tile.getWorld().getBlockState(tile.getPos()).withProperty(ACTIVATED, true));
		}
	}
	
	public BlockLandiaPortalMarker() {
		super(Material.ROCK);
		this.setHarvestLevel("pickaxe", 0);
		this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setDefaultState(blockState.getBaseState().withProperty(ACTIVATED, false));
        this.setUnlocalizedName("landia_portal_marker").setRegistryName("landia_portal_marker");
        MinecraftForge.EVENT_BUS.register(this);
        this.setCreativeTab(LandCraftContent.creativeTab);
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return state.getValue(ACTIVATED) ? 15 : 0;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return LANDIA_PORTAL_MARKER_AABB;
    }
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
    }
	
	@Override
    public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, ACTIVATED);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(ACTIVATED, (meta&0x1) == 1);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(ACTIVATED) ? 1 : 0;
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	
	@Nullable
	@Override
	public TELandiaPortalMarker createTileEntity(World world, IBlockState state) {
		return new TELandiaPortalMarker();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void clAddInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add(I18n.format("tooltip.landia_portal_marker.info"));
	}
}
