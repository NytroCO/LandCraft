package landmaster.landcraft.util;

import java.util.*;

import net.minecraft.block.properties.*;
import net.minecraft.util.*;

public enum Axis implements IStringSerializable {
	Y, X, Z, NONE;
	
	public static final PropertyEnum<Axis> AXIS = PropertyEnum.create("axis", Axis.class);

	@Override
	public String getName() {
		return name().toLowerCase(Locale.US);
	}
	
	@Override
    public String toString() {
    	return getName();
    }
	
	public static Axis fromFacingAxis(EnumFacing.Axis axis) {
        switch (axis)
        {
        case X:
            return X;
        case Y:
            return Y;
        case Z:
            return Z;
        default:
            return NONE;
        }
    }
}
