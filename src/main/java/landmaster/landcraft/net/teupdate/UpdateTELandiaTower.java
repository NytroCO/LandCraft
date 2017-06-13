package landmaster.landcraft.net.teupdate;

import java.util.*;

import io.netty.buffer.*;
import landmaster.landcraft.net.ITEUpdatePacket;
import landmaster.landcraft.tile.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class UpdateTELandiaTower implements ITEUpdatePacket {
	private UUID uuid;
	
	public UpdateTELandiaTower() {}
	public UpdateTELandiaTower(UUID uuid) {
		this.uuid = uuid;
	}
	
	public static IMessage onMessage(TELandiaTower te, UpdateTELandiaTower message, MessageContext ctx) {
		te.setTargetEntity(message.uuid);
		return null;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		if (buf.readBoolean()) {
			uuid = new UUID(buf.readLong(), buf.readLong());
		}
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(uuid != null);
		if (uuid != null) {
			buf.writeLong(uuid.getMostSignificantBits()).writeLong(uuid.getLeastSignificantBits());
		}
	}
	
}
