package landmaster.landcraft.net;

import java.util.*;

import io.netty.buffer.*;
import landmaster.landcore.api.*;
import net.minecraft.client.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class PacketUpdateTE implements IMessage {
	private ByteBuf storedBuf = null;
	private ITEUpdatePacket _payload = null;
	
	private Coord4D coord;
	
	public PacketUpdateTE() {}
	public PacketUpdateTE(Coord4D coord, ITEUpdatePacket payload) {
		this.coord = coord;
		this._payload = payload;
	}
	
	public static IMessage onMessage(PacketUpdateTE message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			try {
				if (message.coord.dimensionId == Minecraft.getMinecraft().world.provider.getDimension()) {
					TileEntity te = Minecraft.getMinecraft().world.getTileEntity(message.coord.pos());
					Optional.ofNullable(PacketHandler.handle(te, message.storedBuf, ctx))
					.ifPresent(PacketHandler.INSTANCE::sendToServer);
				}
			} finally {
				message.storedBuf.release();
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.coord = Coord4D.fromByteBuf(buf);
		this.storedBuf = buf.copy();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		this.coord.toByteBuf(buf);
		_payload.toBytes(buf);
	}
	
}
