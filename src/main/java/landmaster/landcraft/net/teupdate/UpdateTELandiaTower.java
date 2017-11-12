package landmaster.landcraft.net.teupdate;

import java.util.*;

import io.netty.buffer.*;
import landmaster.landcraft.net.*;
import landmaster.landcraft.tile.*;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class UpdateTELandiaTower implements ITEUpdatePacket {
	private UUID uuid;
	private boolean entExists;
	
	public UpdateTELandiaTower() {}
	public UpdateTELandiaTower(UUID uuid) {
		this.uuid = uuid;
		this.entExists = FMLCommonHandler.instance().getMinecraftServerInstance()
				.getEntityFromUuid(this.uuid) != null;
	}
	
	public static IMessage onMessage(TELandiaTower te, UpdateTELandiaTower message, MessageContext ctx) {
		te.setTargetEntity(message.uuid);
		te.setEntExists(message.entExists);
		return null;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		if (buf.readBoolean()) {
			uuid = new UUID(buf.readLong(), buf.readLong());
		}
		entExists = buf.readBoolean();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(uuid != null);
		if (uuid != null) {
			buf.writeLong(uuid.getMostSignificantBits()).writeLong(uuid.getLeastSignificantBits());
		}
		buf.writeBoolean(entExists);
	}
}
