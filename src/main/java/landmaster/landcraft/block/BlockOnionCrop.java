package landmaster.landcraft.block;

import com.google.common.collect.*;

import landmaster.landcraft.content.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class BlockOnionCrop extends BlockCrops {
	private static final ImmutableList<AxisAlignedBB> ONION_AABB = ImmutableList.of(
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.1875D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.3125D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.4375D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D),
			new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5625D, 1.0D));
	
	public BlockOnionCrop() {
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("onion_crop").setRegistryName("onion_crop");
	}
	
	@Override
	protected Item getSeed() {
		return LandCraftContent.onion;
	}
	
	@Override
	protected Item getCrop() {
		return LandCraftContent.onion;
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return ONION_AABB.get(state.getValue(this.getAgeProperty()));
	}
}
