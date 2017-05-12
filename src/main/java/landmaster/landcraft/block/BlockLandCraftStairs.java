package landmaster.landcraft.block;

import landmaster.landcraft.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;

public class BlockLandCraftStairs extends BlockStairs {
	public BlockLandCraftStairs(IBlockState state, String name) {
		super(state);
		this.setUnlocalizedName(name).setRegistryName(name);
		this.useNeighborBrightness = true;
		this.setCreativeTab(LandCraft.creativeTab);
	}
}
