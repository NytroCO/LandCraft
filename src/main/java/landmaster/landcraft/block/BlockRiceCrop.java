package landmaster.landcraft.block;

import landmaster.landcraft.content.*;
import net.minecraft.block.*;
import net.minecraft.item.*;

public class BlockRiceCrop extends BlockCrops {
	public BlockRiceCrop() {
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("rice_crop").setRegistryName("rice_crop");
	}
	
	@Override
	protected Item getSeed() {
		return LandCraftContent.rice;
	}
	
	@Override
	protected Item getCrop() {
		return LandCraftContent.rice;
	}
}
