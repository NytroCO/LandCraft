package landmaster.landcraft.net;

import io.netty.buffer.*;
import landmaster.landcraft.gui.*;
import net.minecraft.client.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class PacketUpdateTEPot implements IMessage, IMessageHandler<PacketUpdateTEPot, IMessage> {
	private int progress, time;
	
	public PacketUpdateTEPot() {}
	public PacketUpdateTEPot(int progress, int time) {
		this.progress = progress;
		this.time = time;
	}
	
	@Override
	public IMessage onMessage(PacketUpdateTEPot message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			if (Minecraft.getMinecraft().currentScreen instanceof GuiTEPot) {
				GuiTEPot gs = ((GuiTEPot)Minecraft.getMinecraft().currentScreen);
				gs.setProgress(message.progress);
				gs.setTime(message.time);
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		progress = buf.readInt();
		time = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(progress);
		buf.writeInt(time);
	}
	
}
