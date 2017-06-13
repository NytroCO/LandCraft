package landmaster.landcraft.net;

import io.netty.buffer.*;
import landmaster.landcore.api.*;
import landmaster.landcraft.net.teupdate.UpdateTELandiaTower;
import landmaster.landcraft.tile.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class PacketRequestUpdateTELandiaTower implements IMessage {
	private Coord4D pos;
	
	public PacketRequestUpdateTELandiaTower() {}
	public PacketRequestUpdateTELandiaTower(Coord4D pos) {
		this.pos = pos;
	}
	
	public static IMessage onMessage(PacketRequestUpdateTELandiaTower message, MessageContext ctx) {
		TileEntity te = message.pos.TE();
		if (te instanceof TELandiaTower) {
			return new PacketUpdateTE(message.pos, new UpdateTELandiaTower(((TELandiaTower)te).getTargetEntity()));
		}
		return null;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		pos = Coord4D.fromByteBuf(buf);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		pos.toByteBuf(buf);
	}
	
}
