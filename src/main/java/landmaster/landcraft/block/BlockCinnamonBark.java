package landmaster.landcraft.block;

import java.util.*;

import com.google.common.collect.*;

import landmaster.landcraft.content.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class BlockCinnamonBark extends Block {
	public static final PropertyEnum<EnumFacing> COVER = PropertyEnum.create("cover", EnumFacing.class);
	
	public static final List<AxisAlignedBB> AABBs = ImmutableList.of(
			new AxisAlignedBB(0, 0, 0, 1, 0.0625, 1),
			new AxisAlignedBB(0, 0.9375, 0, 1, 1, 1),
			new AxisAlignedBB(0, 0, 0, 1, 1, 0.0625),
			new AxisAlignedBB(0, 0, 0.9375, 1, 1, 1),
			new AxisAlignedBB(0, 0, 0, 0.0625, 1, 1),
			new AxisAlignedBB(0.9375, 0, 0, 1, 1, 1));
	
	public BlockCinnamonBark() {
		super(Material.WOOD);
		this.setHardness(0.75f);
		this.setDefaultState(blockState.getBaseState().withProperty(COVER, EnumFacing.NORTH));
		this.setSoundType(SoundType.WOOD);
		Blocks.FIRE.setFireInfo(this, 10, 35);
		this.setUnlocalizedName("cinnamon_bark").setRegistryName("cinnamon_bark");
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, COVER);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABBs.get(state.getValue(COVER).ordinal());
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(COVER, EnumFacing.VALUES[meta]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		EnumFacing facing = (EnumFacing)state.getValue(COVER);
		return facing.ordinal();
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return LandCraftContent.cinnamon;
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
    }
	
	@Override
    public boolean isFullCube(IBlockState state) {
		return false;
	}
}
