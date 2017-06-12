package landmaster.landcraft.net;

import java.util.function.*;

import io.netty.buffer.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;

public final class Handle<T extends TileEntity, M extends ITEUpdatePacket, R extends IMessage> {
	private final Supplier<M> supplier;
	private final HandlerFunction<T, M, R> handler;
	
	public Handle(Supplier<M> supplier, HandlerFunction<T, M, R> handler) {
		this.supplier = supplier;
		this.handler = handler;
	}
	
	public final R handle(T tile, ByteBuf buf, MessageContext ctx) {
		M message = supplier.get();
		message.fromBytes(buf);
		return handler.handle(tile, message, ctx);
	}
}