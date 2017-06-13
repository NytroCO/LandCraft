package landmaster.landcraft.net.teupdate;

import io.netty.buffer.*;
import landmaster.landcraft.net.ITEUpdatePacket;
import landmaster.landcraft.tile.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class UpdateTEBreeder implements ITEUpdatePacket {
	private int fuel;
	private double temp;
	private int product;
	
	public UpdateTEBreeder() {
	}
	public UpdateTEBreeder(TEBreeder te) {
		this(te.getFuel(), te.getTemp(), te.getProduct());
	}
	public UpdateTEBreeder(int fuel, double temp, int product) {
		this.fuel = fuel;
		this.temp = temp;
		this.product = product;
	}
	
	public static IMessage onMessage(TEBreeder te, UpdateTEBreeder message, MessageContext ctx) {
		te.setFuel(message.fuel);
		te.setTemp(message.temp);
		te.setProduct(message.product);
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
