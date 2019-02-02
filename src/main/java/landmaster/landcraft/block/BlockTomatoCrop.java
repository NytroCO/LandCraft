package landmaster.landcraft.block;

import java.util.*;

import javax.annotation.*;

import landmaster.landcraft.content.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.relauncher.*;

public class BlockTomatoCrop extends Block implements IPlantable, IGrowable {
	public static final PropertyInteger AGE = PropertyInteger.create("age", 0, 7);
	private static final PropertyBool HAS_TOMATOES = PropertyBool.create("has_tomatoes");
	
	private static final AxisAlignedBB TOMATO_AABB = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);
	
	public BlockTomatoCrop() {
		super(Material.PLANTS);
		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(AGE, 0)
				.withProperty(HAS_TOMATOES, false));
		this.setTickRandomly(true);
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("tomato_crop").setRegistryName("tomato_crop");
	}
	
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return TOMATO_AABB;
	}
	
	private int getPlantHeight(World worldIn, BlockPos pos) {
		int i;
		i = 1;
		while (worldIn.getBlockState(pos.down(i)).getBlock() == this) {
			++i;
		}
		return i;
	}
	
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		if (worldIn.getBlockState(pos.down()).getBlock() == this || this.checkForDrop(worldIn, pos, state)) {
			if (worldIn.isAirBlock(pos.up())) {
				if (getPlantHeight(worldIn, pos) < 3) {
					int j = state.getValue(AGE);
					
					if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, true)) {
						boolean hasTomatoes = state.getValue(HAS_TOMATOES);
						if (j >= 7) {
							if (hasTomatoes) {
								worldIn.setBlockState(pos.up(), this.getDefaultState());
							}
							worldIn.setBlockState(pos, state.withProperty(AGE, 0).withProperty(HAS_TOMATOES, true), 4);
						} else {
							worldIn.setBlockState(pos, state.withProperty(AGE, j + 1).withProperty(HAS_TOMATOES, hasTomatoes), 4);
						}
						net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state,
								worldIn.getBlockState(pos));
					}
				}
			}
		}
	}
	
	@Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		return super.canSustainPlant(state, world, pos, direction, plantable)
				|| (plantable == this);
	}
	
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState soil = worldIn.getBlockState(pos.down());
        return soil.getBlock().canSustainPlant(soil, worldIn, pos.down(), EnumFacing.UP, this);
	}
	
	/**
	 * Called when a neighboring block was changed and marks that this state
	 * should perform any checks during a neighbor change. Cases may include
	 * when redstone power is updated, cactus blocks popping off due to a
	 * neighboring solid block, etc.
	 */
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		this.checkForDrop(worldIn, pos, state);
	}
	
	private boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
		if (this.canBlockStay(worldIn, pos)) {
			return true;
		} else {
			this.dropBlockAsItem(worldIn, pos, state, 0);
			worldIn.setBlockToAir(pos);
			return false;
		}
	}
	
	private boolean canBlockStay(World worldIn, BlockPos pos) {
		return this.canPlaceBlockAt(worldIn, pos);
	}
	
	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return NULL_AABB;
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return LandCraftContent.tomato_crop_item;
	}
	
	@Override
	public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		super.getDrops(drops, world, pos, state, fortune);
		if (state.getValue(HAS_TOMATOES)) {
			drops.add(new ItemStack(LandCraftContent.tomato));
		}
	}
	
	/**
	 * Used to determine ambient occlusion and culling when rebuilding chunks
	 * for render
	 */
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	public boolean isFullCube(IBlockState state) {
		return false;
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(LandCraftContent.tomato_crop_item);
	}
	
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(AGE, meta & 0x7)
				.withProperty(HAS_TOMATOES, (meta & 0x8) != 0);
	}
	
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.CUTOUT;
	}
	
	/**
	 * Convert the BlockState into the correct metadata value
	 */
	public int getMetaFromState(IBlockState state) {
		return state.getValue(AGE) | ((state.getValue(HAS_TOMATOES) ? 0 : 1) << 3);
	}
	
	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Crop;
	}
	
	@Override
	public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
		return this.getDefaultState();
	}
	
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, AGE, HAS_TOMATOES);
	}
	
	public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_,
			EnumFacing p_193383_4_) {
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient) {
		return getPlantHeight(worldIn, pos) < 3
				&& (worldIn.getBlockState(pos.down()).getBlock() == this || this.checkForDrop(worldIn, pos, state))
				&& worldIn.isAirBlock(pos.up());
	}

	@Override
	public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		return true;
	}

	@Override
	public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
		int newAge = MathHelper.clamp(state.getValue(AGE)+2+rand.nextInt(8), 0, 7);
		boolean hasTomatoes = state.getValue(HAS_TOMATOES);
		
		worldIn.setBlockState(pos, this.getDefaultState()
				.withProperty(AGE, newAge).withProperty(HAS_TOMATOES, hasTomatoes), 2);
		
		if (newAge >= 7) {
			if (hasTomatoes) {
				worldIn.setBlockState(pos.up(), this.getDefaultState());
			}
			worldIn.setBlockState(pos, state.withProperty(AGE, 0).withProperty(HAS_TOMATOES, true), 2);
		}
	}
}
