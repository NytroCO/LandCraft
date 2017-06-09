package landmaster.landcraft.net;

import io.netty.buffer.*;
import landmaster.landcore.api.*;
import landmaster.landcraft.tile.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class PacketRequestUpdateTE implements IMessage {
	private Coord4D pos;
	
	public PacketRequestUpdateTE() {}
	public PacketRequestUpdateTE(Coord4D pos) {
		this.pos = pos;
	}
	
	public static IMessage onMessage(PacketRequestUpdateTE message, MessageContext ctx) {
		TileEntity te = message.pos.TE();
		
		// TODO add to list for more tile entities
		if (te instanceof TELandiaTower) {
			return new PacketUpdateTELandiaTower(new Coord4D(te), ((TELandiaTower)te).getTargetEntity());
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
