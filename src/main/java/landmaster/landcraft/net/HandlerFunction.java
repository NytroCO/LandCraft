package landmaster.landcraft.net;

import net.minecraft.tileentity.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

@FunctionalInterface
public interface HandlerFunction<T extends TileEntity, M extends ITEUpdatePacket, R extends IMessage> {
	R handle(T tile, M message, MessageContext ctx);
}