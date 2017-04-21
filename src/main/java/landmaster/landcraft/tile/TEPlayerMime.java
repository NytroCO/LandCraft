package landmaster.landcraft.tile;

import javax.annotation.*;

import li.cil.oc.api.machine.*;
import li.cil.oc.api.network.*;
import net.minecraftforge.fml.common.*;

@Optional.Interface(iface = "li.cil.oc.api.network.SimpleComponent", modid = "OpenComputers")
public class TEPlayerMime extends TEEnergy
implements RedstoneControl.Provider<TEPlayerMime>, SimpleComponent {
	public TEPlayerMime() {
		super(240000, 1000, 1000);
	}
	
	public @Nonnull RedstoneControl.State getRedstoneState() {
		return RedstoneControl.State.CONTINUOUS;
	}

	@Override
	public String getComponentName() {
		return "player_mime";
	}
	
	@Callback
    @Optional.Method(modid = "OpenComputers")
    public Object[] getPower(Context context, Arguments args) {
		return new Object[] { this.getEnergyStored(null) };
	}
}
