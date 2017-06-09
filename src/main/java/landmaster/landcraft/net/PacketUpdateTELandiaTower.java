package landmaster.landcraft.net;

import java.util.*;

import io.netty.buffer.*;
import landmaster.landcore.api.*;
import landmaster.landcraft.tile.*;
import net.minecraft.client.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class PacketUpdateTELandiaTower implements IMessage {
	private Coord4D pos;
	private UUID uuid;
	
	public PacketUpdateTELandiaTower() {}
	public PacketUpdateTELandiaTower(Coord4D pos, UUID uuid) {
		this.pos = pos;
		this.uuid = uuid;
	}
	
	public static IMessage onMessage(PacketUpdateTELandiaTower message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			TileEntity te = Minecraft.getMinecraft().world.getTileEntity(message.pos.pos());
			if (te instanceof TELandiaTower) {
				((TELandiaTower)te).setTargetEntity(message.uuid);
			}
		});
		return null;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		pos = Coord4D.fromByteBuf(buf);
		if (buf.readBoolean()) {
			uuid = new UUID(buf.readLong(), buf.readLong());
		}
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		pos.toByteBuf(buf);
		buf.writeBoolean(uuid != null);
		if (uuid != null) {
			buf.writeLong(uuid.getMostSignificantBits()).writeLong(uuid.getLeastSignificantBits());
		}
	}
	
}
