package landmaster.landcraft.block;

import landmaster.landcraft.content.*;
import landmaster.landcraft.util.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.state.*;
import net.minecraft.init.*;

public class BlockLandiaWoodSlab extends BlockModSlab<LandiaTreeType> {
	public BlockLandiaWoodSlab() {
		super(Material.WOOD, LandiaTreeType.L_TYPE, LandiaTreeType.class);
		
		Blocks.FIRE.setFireInfo(this, 5, 20);
		
		this.setHardness(2.0F);
		this.setSoundType(SoundType.WOOD);
		this.setUnlocalizedName("landia_wood_slab").setRegistryName("landia_wood_slab");
		this.setCreativeTab(LandCraftContent.creativeTab);
	}
	
	@Override
	public IBlockState getFullBlock(IBlockState state) {
		return LandCraftContent.landia_planks.getDefaultState().withProperty(LandiaTreeType.L_TYPE,
				state.getValue(LandiaTreeType.L_TYPE));
	}
}
