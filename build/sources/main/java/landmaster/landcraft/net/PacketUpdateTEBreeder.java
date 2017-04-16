package landmaster.landcraft.net;

import io.netty.buffer.*;
import landmaster.landcraft.gui.*;
import landmaster.landcraft.tile.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class PacketUpdateTEBreeder implements IMessage, IMessageHandler<PacketUpdateTEBreeder, IMessage> {
	private int fuel;
	private double temp;
	private int product;
	
	public PacketUpdateTEBreeder() {
	}
	public PacketUpdateTEBreeder(TEBreeder te) {
		this(te.getFuel(), te.getTemp(), te.getProduct());
	}
	public PacketUpdateTEBreeder(int fuel, double temp, int product) {
		this.fuel = fuel;
		this.temp = temp;
		this.product = product;
	}
	
	@Override
	public IMessage onMessage(PacketUpdateTEBreeder message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			GuiScreen gs = Minecraft.getMinecraft().currentScreen;
			if (gs instanceof GuiTEBreeder) {
				GuiTEBreeder gb = (GuiTEBreeder)gs;
				gb.setFuel(message.fuel);
				gb.setTemp(message.temp);
				gb.setProduct(message.product);
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		fuel = buf.readInt();
		temp = buf.readDouble();
		product = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(fuel).writeDouble(temp).writeInt(product);
	}
}
