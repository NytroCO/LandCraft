package landmaster.landcraft.net;

import io.netty.buffer.*;

public interface ITEUpdatePacket {
	public void fromBytes(ByteBuf buf);
	public void toBytes(ByteBuf buf);
}
