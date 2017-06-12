package landmaster.landcraft.net;

import java.util.*;
import java.util.function.*;

import gnu.trove.map.hash.*;
import io.netty.buffer.*;
import landmaster.landcraft.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

public class PacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(LandCraft.MODID);
	
	@FunctionalInterface
	public static interface HandlerFunction<T extends TileEntity, M extends ITEUpdatePacket, R extends IMessage> {
		R handle(T tile, M message, MessageContext ctx);
	}
	
	public static final class Handle<T extends TileEntity, M extends ITEUpdatePacket, R extends IMessage> {
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
	
	private static final Map<Class<? extends TileEntity>, Handle<?, ?, ?>> messageMap = new THashMap<>();
	
	public static <T extends TileEntity> void registerTEHandler(Class<T> clazz, Handle<T, ?, ?> handle) {
		messageMap.put(clazz, handle);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static IMessage handle(TileEntity te, ByteBuf buf, MessageContext ctx) {
		return Optional.ofNullable((Handle)messageMap.get(te.getClass()))
				.map(handle -> handle.handle(te, buf, ctx)).orElse(null);
	}
	
	public static void init() {
		INSTANCE.registerMessage(PacketUpdateTE::onMessage, PacketUpdateTE.class, 0, Side.CLIENT);
		INSTANCE.registerMessage(PacketRequestUpdateTELandiaTower::onMessage, PacketRequestUpdateTELandiaTower.class, 1, Side.SERVER);
	}
}
