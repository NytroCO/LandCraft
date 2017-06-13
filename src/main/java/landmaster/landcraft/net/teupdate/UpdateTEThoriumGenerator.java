package landmaster.landcraft.net.teupdate;

import io.netty.buffer.*;
import landmaster.landcraft.net.ITEUpdatePacket;
import landmaster.landcraft.tile.*;
import net.minecraft.nbt.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public class UpdateTEThoriumGenerator implements ITEUpdatePacket {
	private int progress, energy;
	private FluidStack fs;
	
	public UpdateTEThoriumGenerator() {}
	public UpdateTEThoriumGenerator(int energy, int progress, FluidStack fs) {
		this.energy = energy;
		this.progress = progress;
		this.fs = fs;
	}
	
	public static IMessage onMessage(TEThoriumGenerator te, UpdateTEThoriumGenerator message, MessageContext ctx) {
		te.setProgress(message.progress);
		te.setFluid(message.fs);
		te.setEnergyStored(null, message.energy);
		return null;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		energy = buf.readInt();
		progress = buf.readInt();
		fs = FluidStack.loadFluidStackFromNBT(ByteBufUtils.readTag(buf));
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(energy);
		buf.writeInt(progress);
		ByteBufUtils.writeTag(buf, fs != null ? fs.writeToNBT(new NBTTagCompound()) : null);
	}
	
}
