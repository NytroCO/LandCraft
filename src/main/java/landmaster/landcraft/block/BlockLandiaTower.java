package landmaster.landcraft.block;

import javax.annotation.*;

import landmaster.landcraft.config.*;
import landmaster.landcraft.content.*;
import landmaster.landcraft.tile.*;
import mcjty.lib.compat.*;
import mcjty.lib.tools.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;

public class BlockLandiaTower extends CompatBlock {
	public static final int MAX_POSITION = 3;
	
	public static final IProperty<Integer> POSITION = PropertyInteger.create("position", 0, MAX_POSITION);
	
	public BlockLandiaTower() {
		super(Material.ROCK);
		this.setDefaultState(blockState.getBaseState().withProperty(POSITION, 0));
		this.setHardness(9.5f);
		this.setResistance(23.0f);
		this.setHarvestLevel("pickaxe", 8);
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setSoundType(SoundType.STONE);
		this.setUnlocalizedName("landia_tower").setRegistryName("landia_tower");
	}
	
	public static final int LANDIA_CHECK_MSG_SIZE = 7;
	public static final int LANDIA_NEARCOMPLETE_MSG_SIZE = 2;
	
	@Override
	protected boolean clOnBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn,
			EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote)
			return true;
		if (worldIn.provider.getDimension() != Config.landiaDimensionID) // Only works on Landia.
			return false;
		
		for (int i=0; i<LANDIA_CHECK_MSG_SIZE; ++i) {
			ChatTools.addChatMessage(playerIn,
					new TextComponentTranslation("msg.landia_tower.check."+i,
							TELandiaTower.SUB_HEIGHT, TELandiaTower.SUB_DIST - 1));
		}
		
		TileEntity tileGeneral = worldIn.getTileEntity(pos.add(0, -state.getValue(POSITION), 0));
		if (tileGeneral instanceof TELandiaTower) {
			if (((TELandiaTower) tileGeneral).checkTowers()) {
				for (int i=0; i<LANDIA_NEARCOMPLETE_MSG_SIZE; ++i) {
					ChatTools.addChatMessage(playerIn,
							new TextComponentTranslation("msg.landia_tower.nearcomplete."+i));
				}
			}
		}
		return true;
	}
	
	public boolean testBlockPlacement(World worldIn, BlockPos pos) { // For use by the itemblock.
		for (int i = 1; i <= MAX_POSITION; ++i) {
			if (!worldIn.isAirBlock(pos.add(0, i, 0))) return false;
		}
		return true;
	}
	
	@Override
	public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
		if (state.getValue(POSITION) == 0) {
			for (int i = 1; i <= MAX_POSITION; ++i) {
				worldIn.setBlockState(pos.add(0, i, 0), this.getDefaultState().withProperty(POSITION, i), 0x2);
			}
		}
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, POSITION);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(POSITION, meta);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(POSITION);
	}
	
	private boolean verifyNeighborPos(World worldIn, IBlockState state0, BlockPos pos0) {
		int val = state0.getValue(POSITION);
		IBlockState statem1 = worldIn.getBlockState(pos0.add(0, -1, 0)),
				state1 = worldIn.getBlockState(pos0.add(0, 1, 0));
		
		return (val <= 0 || (statem1.getBlock() == this && statem1.getValue(POSITION) == val - 1))
				&& (val >= MAX_POSITION || (state1.getBlock() == this && state1.getValue(POSITION) == val + 1));
	}
	
	@Override
	protected void clOnNeighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
		if (!verifyNeighborPos(worldIn, state, pos)) {
			worldIn.setBlockToAir(pos);
		}
	}
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return state.getValue(POSITION) == 0; // bottom only
	}
	
	@Nullable
	@Override
	public TELandiaTower createTileEntity(World world, IBlockState state) {
		if (this.hasTileEntity(state)) {
			return new TELandiaTower();
		}
		return null;
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
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		double shrink = state.getValue(POSITION) / (2.0 * (MAX_POSITION + 1));
		return new AxisAlignedBB(shrink, 0, shrink, 1 - shrink, 1, 1 - shrink);
	}
}
