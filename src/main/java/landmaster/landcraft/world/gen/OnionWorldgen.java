package landmaster.landcraft.world.gen;

import landmaster.landcraft.config.*;
import landmaster.landcraft.content.*;
import net.minecraft.block.state.*;

public class OnionWorldgen extends LandiaPlantWorldgen {
	
	@Override
	protected IBlockState getState() {
		return LandCraftContent.wild_onion.getDefaultState();
	}
	
	@Override
	protected int getAmount() {
		return Config.onion_per_chunk;
	}

	@Override
	protected long randomUniquifier() {
		return 14237;
	}
	
}
