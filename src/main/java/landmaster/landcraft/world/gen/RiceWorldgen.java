package landmaster.landcraft.world.gen;

import landmaster.landcraft.config.*;
import landmaster.landcraft.content.*;
import net.minecraft.block.state.*;

public class RiceWorldgen extends LandiaPlantWorldgen {
	
	@Override
	protected IBlockState getState() {
		return LandCraftContent.wild_rice.getDefaultState();
	}
	
	@Override
	protected int getAmount() {
		return Config.rice_per_chunk;
	}

	@Override
	protected long randomUniquifier() {
		return 2953;
	}
	
}
