package landmaster.landcraft.util;

import java.util.*;

import net.minecraft.block.properties.*;
import net.minecraft.util.*;

public enum LandiaTreeType implements IStringSerializable {
    CINNAMON, OLIVE;
	
	public static final PropertyEnum<LandiaTreeType> L_TYPE = PropertyEnum.create("l_type", LandiaTreeType.class);
	
    @Override
    public String getName() {
        return name().toLowerCase(Locale.US);
    }
    
    @Override
    public String toString() {
    	return getName();
    }
}