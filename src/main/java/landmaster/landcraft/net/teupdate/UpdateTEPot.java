package landmaster.landcraft.net.teupdate;

import io.netty.buffer.*;
import landmaster.landcraft.net.ITEUpdatePacket;
import landmaster.landcraft.tile.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class UpdateTEPot implements ITEUpdatePacket {
	private int energy, progress, time;
	private FluidStack fs;
	
	public UpdateTEPot() {}
	public UpdateTEPot(int energy, int progress, int time) {
		this.progress = progress;
		this.time = time;
	}
	
	public static IMessage onMessage(TEPot te, UpdateTEPot message, MessageContext ctx) {
		te.setEnergyStored(null, message.energy);
		te.setProgress(message.progress);
		te.setClientTime(message.time);
		te.setFluid(message.fs);
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		energy = buf.readInt();
		progress = buf.readInt();
		time = buf.readInt();
		fs = FluidStack.loadFluidStackFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(energy);
		buf.writeInt(progress);
		buf.writeInt(time);
		ByteBufUtils.writeTag(buf, fs != null ? fs.writeToNBT(new NBTTagCompound()) : null);
	}
	
}
