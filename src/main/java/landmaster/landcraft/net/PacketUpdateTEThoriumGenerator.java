package landmaster.landcraft.net;

import io.netty.buffer.*;
import landmaster.landcraft.gui.*;
import net.minecraft.client.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class PacketUpdateTEThoriumGenerator implements IMessage {
	private int progress;
	
	public PacketUpdateTEThoriumGenerator() {}
	public PacketUpdateTEThoriumGenerator(int progress) {
		this.progress = progress;
	}
	
	public static IMessage onMessage(PacketUpdateTEThoriumGenerator message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			if (Minecraft.getMinecraft().currentScreen instanceof GuiTEThoriumGenerator) {
				((GuiTEThoriumGenerator)Minecraft.getMinecraft().currentScreen).setProgress(message.progress);
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		progress = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(progress);
	}
	
}
