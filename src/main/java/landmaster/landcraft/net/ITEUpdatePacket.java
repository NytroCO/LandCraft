package landmaster.landcraft.net;

import io.netty.buffer.*;

public interface ITEUpdatePacket {
	void fromBytes(ByteBuf buf);
	void toBytes(ByteBuf buf);
}
