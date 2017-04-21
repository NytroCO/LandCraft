package landmaster.landcraft.net;

import io.netty.buffer.*;
import landmaster.landcraft.gui.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.nbt.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class PacketUpdateClientFluid implements IMessage, IMessageHandler<PacketUpdateClientFluid, IMessage> {
	private FluidStack fs;
	
	public PacketUpdateClientFluid() {}
	public PacketUpdateClientFluid(FluidStack fs) {
		this.fs = fs;
	}
	
	@Override
	public IMessage onMessage(PacketUpdateClientFluid message, MessageContext ctx) {
		Minecraft.getMinecraft().addScheduledTask(() -> {
			GuiScreen gs = Minecraft.getMinecraft().currentScreen;
			if (gs instanceof IGuiFluid) {
				((IGuiFluid)gs).setFluidStack(message.fs);
			}
		});
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		fs = FluidStack.loadFluidStackFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeTag(buf, fs != null ? fs.writeToNBT(new NBTTagCompound()) : null);
	}
}
