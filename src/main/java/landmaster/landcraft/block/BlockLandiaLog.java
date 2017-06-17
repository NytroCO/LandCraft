package landmaster.landcraft.block;

import landmaster.landcore.api.block.*;
import landmaster.landcraft.content.*;
import landmaster.landcraft.util.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

public class BlockLandiaLog extends Block implements IMetaBlockName {
	public BlockLandiaLog() {
		super(Material.WOOD);
		
		this.setDefaultState(blockState.getBaseState()
				.withProperty(Axis.AXIS, Axis.Y));
		
		this.setHarvestLevel("axe", -1);
		this.setHardness(1.5F);
		this.setResistance(5F);
		this.setSoundType(SoundType.WOOD);
		Blocks.FIRE.setFireInfo(this, 5, 20);
		this.setUnlocalizedName("landia_log").setRegistryName("landia_log");
		this.setCreativeTab(LandCraftContent.creativeTab);
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
	    return new BlockStateContainer(this, LandiaTreeType.L_TYPE, Axis.AXIS);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState()
				.withProperty(LandiaTreeType.L_TYPE, LandiaTreeType.values()[meta & 0x3])
				.withProperty(Axis.AXIS, Axis.values()[meta >> 2]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return (state.getValue(Axis.AXIS).ordinal() << 2)
				| (state.getValue(LandiaTreeType.L_TYPE).ordinal() & 0x3);
	}
	
	@Override
	public int damageDropped(IBlockState state) {
	    return getMetaFromState(state);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (LandiaTreeType type: LandiaTreeType.values()) {
			list.add(new ItemStack(this, 1, getMetaFromState(getDefaultState()
					.withProperty(LandiaTreeType.L_TYPE, type))));
		}
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
	}
	
	@Override
	public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
		IBlockState state = world.getBlockState(pos);
		for (IProperty<?> prop : state.getProperties().keySet()) {
			if (prop.getName().equals(Axis.AXIS.getName())) {
				world.setBlockState(pos, state.cycleProperty(prop));
				return true;
			}
		}
		return false;
	}
	
	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		switch (rot) {
		case COUNTERCLOCKWISE_90:
		case CLOCKWISE_90:
			switch (state.getValue(Axis.AXIS)) {
			case X:
				return state.withProperty(Axis.AXIS, Axis.Z);
			case Z:
				return state.withProperty(Axis.AXIS, Axis.X);
			default:
				return state;
			}
		default:
			return state;
		}
	}

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        if (worldIn.isAreaLoaded(pos.add(-5, -5, -5), pos.add(5, 5, 5)))
        {
            for (BlockPos.MutableBlockPos blockpos : BlockPos.getAllInBoxMutable(pos.add(-4, -4, -4), pos.add(4, 4, 4)))
            {
                IBlockState iblockstate = worldIn.getBlockState(blockpos);

                if (iblockstate.getBlock().isLeaves(iblockstate, worldIn, blockpos))
                {
                    iblockstate.getBlock().beginLeavesDecay(iblockstate, worldIn, blockpos);
                }
            }
        }
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the IBlockstate
     */
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getStateFromMeta(meta).withProperty(Axis.AXIS, Axis.fromFacingAxis(facing.getAxis()));
    }
	
	@Override
	public boolean canSustainLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
		return true;
	}
	
    @Override
    public boolean isWood(IBlockAccess world, BlockPos pos) {
    	return true;
    }

    @Override
	public String getSpecialName(ItemStack stack) {
		return getStateFromMeta(stack.getMetadata()).getValue(LandiaTreeType.L_TYPE).toString();
	}
}
