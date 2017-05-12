package landmaster.landcraft.world;

import java.util.*;

import landmaster.landcraft.*;
import landmaster.landcraft.block.*;
import landmaster.landcraft.util.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.init.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;

public class LandiaTreeGenerator extends BaseTreeGenerator
{
    public final int minTreeHeight;

    public final int treeHeightRange;

    public final IBlockState log;

    public final IBlockState leaves;

    public final boolean seekHeight;

    public final boolean isSapling;

    public LandiaTreeGenerator(int treeHeight, int treeRange, IBlockState log, IBlockState leaves, boolean seekHeight, boolean isSapling)
    {
        this.minTreeHeight = treeHeight;
        this.treeHeightRange = treeRange;
        this.log = log;
        this.leaves = leaves;
        this.seekHeight = seekHeight;
        this.isSapling = isSapling;
    }

    public LandiaTreeGenerator(int treeHeight, int treeRange, IBlockState log, IBlockState leaves)
    {
        this(treeHeight, treeRange, log, leaves, true, false);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
    }

    @Override
    public void generateTree(Random random, World world, BlockPos pos)
    {
        int height = random.nextInt(this.treeHeightRange) + this.minTreeHeight;
        if (this.seekHeight)
        {
            pos = this.findGround(world, pos);
            if (pos.getY() < 0)
            {
                return;
            }
        }

        int yPos = pos.getY();

        if (yPos >= 1 && yPos + height + 1 <= 256)
        {
            IBlockState state = world.getBlockState(pos.down());
            Block soil = state.getBlock();
            boolean isSoil = (soil != null && soil.canSustainPlant(state, world, pos.down(), EnumFacing.UP, LandCraft.landia_sapling));

            if (isSoil)
            {
                if (!this.checkClear(world, pos.getX(), yPos, pos.getY(), height))
                {
                    return;
                }

                soil.onPlantGrow(state, world, pos.down(), pos);
                this.placeCanopy(world, random, pos, height);
                this.placeTrunk(world, pos, height);
            }
        }
    }

    boolean checkClear(World world, int x, int y, int z, int treeHeight)
    {
        for (int yPos = 0; yPos < treeHeight + 1; yPos++)
        {
            int range = 1;

            if (yPos == 0)
            {
                range = 0;
            }
            else if (yPos >= treeHeight - 1)
            {
                range = 2;
            }

            for (int xPos = range; xPos <= range; xPos++)
            {
                for (int zPos = range; zPos <= range; zPos++)
                {
                    BlockPos blockpos = new BlockPos(x + xPos, y + yPos, z + zPos);
                    IBlockState state = world.getBlockState(blockpos);

                    if (state.getBlock() != null && state.getBlock() != LandCraft.landia_sapling || !state.getBlock().isLeaves(state, world, blockpos))
                    {
                        return true;
                    }
                }
            }
        }
        return true;
    }

    BlockPos findGround(World world, BlockPos pos)
    {
        int returnHeight = 0;

        int height = pos.getY();

        if (world.getWorldType() == WorldType.FLAT && this.isSapling)
        {
            do
            {
                BlockPos position = new BlockPos(pos.getX(), height, pos.getZ());

                Block block = world.getBlockState(position).getBlock();

                if ((block == Blocks.DIRT || block == Blocks.GRASS) && !world.getBlockState(position.up()).isFullBlock())
                {
                    returnHeight = height + 1;
                    break;
                }

                height--;
            }
            while (height > 1);

            return new BlockPos(pos.getX(), returnHeight, pos.getZ());
        }
        else
        {

            do
            {
                BlockPos position = new BlockPos(pos.getX(), height, pos.getZ());

                Block block = world.getBlockState(position).getBlock();

                if ((block == Blocks.DIRT || block == Blocks.GRASS) && !world.getBlockState(position.up()).isFullBlock())
                {
                    returnHeight = height + 1;
                    break;
                }

                height--;
            }
            while (height > 64);

            return new BlockPos(pos.getX(), returnHeight, pos.getZ());
        }
    }

    protected void placeCanopy(World world, Random random, BlockPos pos, int height)
    {
        for (int y = pos.getY() - 3 + height; y <= pos.getY() + height; ++y)
        {
            int subract = y - (pos.getY() + height);
            int subract2 = 1 - subract / 2;

            for (int x = pos.getX() - subract2; x <= pos.getX() + subract2; ++x)
            {
                int mathX = x - pos.getX();

                for (int z = pos.getZ() - subract2; z <= pos.getZ() + subract2; ++z)
                {
                    int mathZ = z - pos.getZ();

                    if (Math.abs(mathX) != subract2 || Math.abs(mathZ) != subract2 || random.nextInt(2) != 0 && subract != 0)
                    {
                        BlockPos blockpos = new BlockPos(x, y, z);
                        IBlockState state = world.getBlockState(blockpos);

                        if (state.getBlock().isAir(state, world, blockpos) || state.getBlock().canBeReplacedByLeaves(state, world, blockpos))
                        {
                            world.setBlockState(blockpos, this.leaves, 2);
                        }
                    }
                }
            }
        }
    }

    protected void placeTrunk(World world, BlockPos pos, int height)
    {
        for (int localHeight = 0; localHeight < height; ++localHeight)
        {
            BlockPos blockpos = new BlockPos(pos.getX(), pos.getY() + localHeight, pos.getZ());
            IBlockState state = world.getBlockState(blockpos);
            Block block = state.getBlock();

            if (block.isAir(state, world, blockpos) || block == null || block.isLeaves(state, world, blockpos))
            {
                world.setBlockState(blockpos, this.log, 2);
            }
            
            // for cinnamon tree
            if (this.log.getValue(LandiaTreeType.L_TYPE) == LandiaTreeType.CINNAMON) {
	            for (EnumFacing facing: EnumFacing.HORIZONTALS) {
	            	BlockPos spos = blockpos.offset(facing);
	            	IBlockState sstate = world.getBlockState(spos);
	            	if (world.isAirBlock(spos) || sstate.getBlock() == null) {
	            		world.setBlockState(spos,
	            				LandCraft.cinnamon_bark.getDefaultState()
	            				.withProperty(BlockCinnamonBark.COVER, facing.getOpposite()),
	            				2);
	            	}
	            }
            }
        }
    }
}