package landmaster.landcraft.asm;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import landmaster.landcraft.api.*;
import net.minecraftforge.fml.relauncher.*;

@IFMLLoadingPlugin.Name(ModInfo.NAME)
@IFMLLoadingPlugin.TransformerExclusions("landmaster.landcraft.asm")
public class LoadingPlugin implements IFMLLoadingPlugin {
	public static final Logger coreLog = LogManager.getLogger("LandCraft coremod");
	
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
