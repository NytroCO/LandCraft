package landmaster.landcraft.net.teupdate;

import io.netty.buffer.*;
import landmaster.landcraft.net.ITEUpdatePacket;
import landmaster.landcraft.tile.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class UpdateTEPlayerMime implements ITEUpdatePacket {
	private int energy;
	
	public UpdateTEPlayerMime() {}

    public UpdateTEPlayerMime(int energy) {
		this.energy = energy;
	}
	
	public static IMessage onMessage(TEPlayerMime te, UpdateTEPlayerMime message, MessageContext ctx) {
		te.setEnergyStored(null, message.energy);
		return null;
	}
	@Override
	public void fromBytes(ByteBuf buf) {
		energy = buf.readInt();
	}
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(energy);
	}
}
