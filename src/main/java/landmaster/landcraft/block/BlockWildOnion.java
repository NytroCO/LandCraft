package landmaster.landcraft.block;

import java.util.*;

import landmaster.landcraft.content.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class BlockWildOnion extends BlockBush {
	public static final AxisAlignedBB W_ONION_BOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5625D, 1.0D);
	
	public BlockWildOnion() {
		this.setUnlocalizedName("wild_onion").setRegistryName("wild_onion");
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return W_ONION_BOX;
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return LandCraftContent.onion;
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 2 + random.nextInt(3);
	}
	
	@Override
	public int quantityDroppedWithBonus(int fortune, Random random) {
		return super.quantityDroppedWithBonus(fortune, random) + random.nextInt(fortune);
	}
	
	@Override
    public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player){
        return false;
    }
}
