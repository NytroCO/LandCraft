package landmaster.landcraft.asm;

import java.util.*;

import landmaster.landcraft.*;
import net.minecraftforge.fml.relauncher.*;

@IFMLLoadingPlugin.Name(LandCraft.NAME)
@IFMLLoadingPlugin.TransformerExclusions("landmaster.landcraft.asm")
public class LoadingPlugin implements IFMLLoadingPlugin {
	
	@Override
	public String[] getASMTransformerClass() {
		return new String[] { Transform.class.getName() };
	}
	
	@Override
	public String getModContainerClass() {
		return null;
	}
	
	@Override
	public String getSetupClass() {
		return null;
	}
	
	@Override
	public void injectData(Map<String, Object> data) {
	}
	
	@Override
	public String getAccessTransformerClass() {
		return null;
	}
	
}
