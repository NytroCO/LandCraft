package landmaster.landcraft.block;

import java.util.*;

import javax.annotation.*;

import com.google.common.collect.*;

import landmaster.landcore.api.block.*;
import landmaster.landcraft.*;
import landmaster.landcraft.util.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.creativetab.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

public class BlockLandiaLeaves extends BlockLeaves implements IMetaBlockName {
	public BlockLandiaLeaves() {
		this.setCreativeTab(LandCraft.creativeTab);
		Blocks.FIRE.setFireInfo(this, 30, 60);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(CHECK_DECAY, false)
				.withProperty(DECAYABLE, true));
		this.setUnlocalizedName("landia_leaves").setRegistryName("landia_leaves");
	}
	
	@Override
	public void updateTick(World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, Random rand) {
		super.updateTick(worldIn, pos, state, rand);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(@Nonnull Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		this.func_149666_a(itemIn, tab, list);
	}
	
	public void func_149666_a(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		for (LandiaTreeType type : LandiaTreeType.values()) {
			list.add(new ItemStack(this, 1,
					this.getMetaFromState(this.getDefaultState().withProperty(LandiaTreeType.L_TYPE, type))));
		}
	}
	
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return Blocks.LEAVES.isOpaqueCube(state);
	}
	
	@Nonnull
	@SideOnly(Side.CLIENT)
	@Override
	public BlockRenderLayer getBlockLayer() {
		return Blocks.LEAVES.getBlockLayer();
	}
	
	@Override
	public boolean shouldSideBeRendered(@Nonnull IBlockState blockState, @Nonnull IBlockAccess blockAccess,
			@Nonnull BlockPos pos, @Nonnull EnumFacing side) {
		// isOpaqueCube returns !leavesFancy to us. We have to fix the variable
		// before calling super
		this.leavesFancy = !Blocks.LEAVES.isOpaqueCube(blockState);
		
		return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}
	
	@Override
	protected int getSaplingDropChance(IBlockState state) {
		return 25;
	}
	
	// sapling item
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(LandCraft.landia_sapling);
	}
	
	@Override
	protected void dropApple(World worldIn, BlockPos pos, IBlockState state, int chance) {
	}
	
	// sapling meta
	@Override
	public int damageDropped(IBlockState state) {
		// only 1st 2 bits
		return (state.getValue(LandiaTreeType.L_TYPE)).ordinal() & 3;
	}
	
	// item dropped on silktouching
	@Override
	protected ItemStack getSilkTouchDrop(@Nonnull IBlockState state) {
		return new ItemStack(Item.getItemFromBlock(this), 1, (state.getValue(LandiaTreeType.L_TYPE)).ordinal() & 3);
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, LandiaTreeType.L_TYPE, CHECK_DECAY, DECAYABLE);
	}
	
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		int type = meta % 4;
		if (type < 0 || type >= LandiaTreeType.values().length) {
			type = 0;
		}
		LandiaTreeType logtype = LandiaTreeType.values()[type];
		return this.getDefaultState().withProperty(LandiaTreeType.L_TYPE, logtype)
				.withProperty(DECAYABLE, (meta & 4) == 0).withProperty(CHECK_DECAY, (meta & 8) > 0);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		// only 1st 2 bits
		int meta = (state.getValue(LandiaTreeType.L_TYPE)).ordinal() & 3;
		
		if (!state.getValue(DECAYABLE)) {
			meta |= 4;
		}
		
		if (state.getValue(CHECK_DECAY)) {
			meta |= 8;
		}
		
		return meta;
	}
	
	@Nonnull
	@Override
	public BlockPlanks.EnumType getWoodType(int meta) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		IBlockState state = world.getBlockState(pos);
		return Lists.newArrayList(this.getSilkTouchDrop(state));
	}
	
	@Override
	public boolean isLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public String getSpecialName(ItemStack stack) {
		return getStateFromMeta(stack.getMetadata()).getValue(LandiaTreeType.L_TYPE).toString();
	}
}
