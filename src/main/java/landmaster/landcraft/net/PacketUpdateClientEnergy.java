package landmaster.landcraft.net;

import io.netty.buffer.*;
import landmaster.landcraft.gui.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class PacketUpdateClientEnergy implements IMessage {
	private int energy = 0;
	
	public PacketUpdateClientEnergy() {}
	public PacketUpdateClientEnergy(int energy) { this.energy = energy; }
	
	public static IMessage onMessage(PacketUpdateClientEnergy message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			GuiScreen gs = Minecraft.getMinecraft().currentScreen;
			if (gs instanceof GuiEnergy) {
				GuiEnergy ge = (GuiEnergy)gs;
				ge.setEnergy(message.energy);
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		energy = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(energy);
	}
}
