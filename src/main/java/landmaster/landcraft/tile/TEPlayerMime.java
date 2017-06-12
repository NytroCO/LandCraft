package landmaster.landcraft.tile;

import javax.annotation.*;

import landmaster.landcraft.net.*;

public class TEPlayerMime extends TEEnergy
implements RedstoneControl.Provider<TEPlayerMime> {
	public TEPlayerMime() {
		super(240000, 1000, 1000);
	}
	
	static {
		PacketHandler.registerTEHandler(TEPlayerMime.class,
				new Handle<>(UpdateTEPlayerMime::new, UpdateTEPlayerMime::onMessage));
	}
	
	public @Nonnull RedstoneControl.State getRedstoneState() {
		return RedstoneControl.State.CONTINUOUS;
	}
}
