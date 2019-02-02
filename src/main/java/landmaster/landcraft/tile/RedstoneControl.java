package landmaster.landcraft.tile;

import net.minecraft.tileentity.*;

public class RedstoneControl {
	public enum State {
		CONTINUOUS, PULSE, NONE
	}
	
	public interface Provider<T extends TileEntity & Provider<T>> {
		RedstoneControl.State getRedstoneState();
		default boolean isEnabledPulse(T tile) {
			return true;
		}
		default boolean isEnabled(T tile) {
			switch (tile.getRedstoneState()) {
			case CONTINUOUS:
				return !tile.getWorld().isBlockPowered(tile.getPos());
			case PULSE:
				return isEnabledPulse(tile);
			case NONE:
				return true;
			}
			return true;
		}
	}
}
