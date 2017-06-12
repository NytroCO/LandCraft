package landmaster.landcraft.world.gen;

import java.util.*;

import landmaster.landcraft.block.*;
import landmaster.landcraft.content.*;
import landmaster.landcraft.util.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class LandiaTreeGenerator extends TreeGenerator {
	public final int minTreeHeight;
	
	public final int treeHeightRange;
	
	public final IBlockState log;
	
	public final IBlockState leaves;
	
	public final boolean seekHeight;
	
	public final boolean isSapling;
	
	public LandiaTreeGenerator(int treeHeight, int treeRange, IBlockState log, IBlockState leaves, boolean seekHeight,
			boolean isSapling) {
		this.minTreeHeight = treeHeight;
		this.treeHeightRange = treeRange;
		this.log = log;
		this.leaves = leaves;
		this.seekHeight = seekHeight;
		this.isSapling = isSapling;
	}
	
	public LandiaTreeGenerator(int treeHeight, int treeRange, IBlockState log, IBlockState leaves) {
		this(treeHeight, treeRange, log, leaves, true, false);
	}
	
	@Override
	public void generateTree(Random rand, World worldIn, BlockPos position) {
		int heightRange = rand.nextInt(this.treeHeightRange) + this.minTreeHeight;
		
		if (this.seekHeight) {
			position = this.findGround(worldIn, position);
			
			if (position.getY() < 0) {
				return;
			}
		}
		
		if (position.getY() >= 1 && position.getY() + heightRange + 1 <= 256) {
			IBlockState state = worldIn.getBlockState(position.down());
			Block soil = state.getBlock();
			boolean isSoil = (soil != null && soil.canSustainPlant(state, worldIn, position.down(), EnumFacing.UP,
					LandCraftContent.landia_sapling));
			
			if (isSoil) {
				boolean canGrow = checkIfCanGrow(position, heightRange, worldIn);
				if (canGrow) {
					soil.onPlantGrow(state, worldIn, position.down(), position);
					this.placeCanopy(worldIn, rand, position, heightRange);
					this.placeTrunk(worldIn, position, heightRange);
				}
			}
		}
	}
	
	private boolean checkIfCanGrow(BlockPos position, int heightRange, World worldIn) {
		return true;
	}
	
	BlockPos findGround(World world, BlockPos pos) {
		int returnHeight = 0;
		
		int height = pos.getY();
		
		if (world.getWorldType() == WorldType.FLAT && this.isSapling) {
			do {
				BlockPos position = new BlockPos(pos.getX(), height, pos.getZ());
				
				IBlockState state = world.getBlockState(position);
				Block block = state.getBlock();
				@SuppressWarnings("deprecation") // we don't have a player here, so might as well use this version
				ItemStack is = block.getItem(world, position, state);
				
				if ((Utils.matchesOre(is, "dirt") || Utils.matchesOre(is, "grass"))
						&& !world.getBlockState(position.up()).isFullBlock()) {
					returnHeight = height + 1;
					break;
				}
				
				height--;
			} while (height > 1);
			
			return new BlockPos(pos.getX(), returnHeight, pos.getZ());
		} else {
			
			do {
				BlockPos position = new BlockPos(pos.getX(), height, pos.getZ());
				
				IBlockState state = world.getBlockState(position);
				Block block = state.getBlock();
				@SuppressWarnings("deprecation")
				ItemStack is = block.getItem(world, position, state);
				
				if ((Utils.matchesOre(is, "dirt") || Utils.matchesOre(is, "grass"))
						&& !world.getBlockState(position.up()).isFullBlock()) {
					returnHeight = height + 1;
					break;
				}
				
				height--;
			} while (height > 60);
			
			return new BlockPos(pos.getX(), returnHeight, pos.getZ());
		}
	}
	
	protected void placeCanopy(World world, Random random, BlockPos pos, int height) {
		for (int y = pos.getY() - 3 + height; y <= pos.getY() + height; ++y) {
			int subract = y - (pos.getY() + height);
			int subract2 = 1 - subract / 2;
			
			for (int x = pos.getX() - subract2; x <= pos.getX() + subract2; ++x) {
				int mathX = x - pos.getX();
				
				for (int z = pos.getZ() - subract2; z <= pos.getZ() + subract2; ++z) {
					int mathZ = z - pos.getZ();
					
					if (Math.abs(mathX) != subract2 || Math.abs(mathZ) != subract2
							|| random.nextInt(2) != 0 && subract != 0) {
						BlockPos blockpos = new BlockPos(x, y, z);
						IBlockState state = world.getBlockState(blockpos);
						
						if (state.getBlock().isAir(state, world, blockpos)
								|| state.getBlock().canBeReplacedByLeaves(state, world, blockpos)) {
							world.setBlockState(blockpos, this.leaves, 2);
						}
					}
				}
			}
		}
	}
	
	protected void placeTrunk(World world, BlockPos pos, int height) {
		for (int localHeight = 0; localHeight < height; ++localHeight) {
			BlockPos blockpos = new BlockPos(pos.getX(), pos.getY() + localHeight, pos.getZ());
			IBlockState state = world.getBlockState(blockpos);
			Block block = state.getBlock();
			
			if (world.isAirBlock(blockpos) || block.isLeaves(state, world, blockpos)) {
				world.setBlockState(blockpos, this.log, 2);
			}
			
			// for cinnamon tree
			if (this.log.getValue(LandiaTreeType.L_TYPE) == LandiaTreeType.CINNAMON) {
				for (EnumFacing facing : EnumFacing.HORIZONTALS) {
					BlockPos spos = blockpos.offset(facing);
					if (world.isAirBlock(spos)) {
						world.setBlockState(spos, LandCraftContent.cinnamon_bark.getDefaultState()
								.withProperty(BlockCinnamonBark.COVER, facing.getOpposite()), 2);
					}
				}
			}
		}
	}
}
