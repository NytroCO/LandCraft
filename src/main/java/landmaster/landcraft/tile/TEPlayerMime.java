package landmaster.landcraft.tile;

import javax.annotation.*;

public class TEPlayerMime extends TEEnergy implements RedstoneControl.Provider<TEPlayerMime> {
	public TEPlayerMime() {
		super(240000, 1000, 1000);
	}
	
	public @Nonnull RedstoneControl.State getRedstoneState() {
		return RedstoneControl.State.CONTINUOUS;
	}
}
