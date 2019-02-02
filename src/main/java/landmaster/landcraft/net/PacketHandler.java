package landmaster.landcraft.net;

import java.util.*;

import gnu.trove.map.hash.*;
import io.netty.buffer.*;
import landmaster.landcraft.api.ModInfo;
import net.minecraft.tileentity.*;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

public class PacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(ModInfo.MODID);
	
	private static final Map<Class<? extends TileEntity>, Handle<?, ?, ?>> messageMap = new THashMap<>();
	
	public static <T extends TileEntity> void registerTEHandler(Class<T> clazz, Handle<T, ?, ?> handle) {
		messageMap.put(clazz, handle);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static IMessage handle(TileEntity te, ByteBuf buf, MessageContext ctx) {
		return Optional.ofNullable((Handle)messageMap.get(te.getClass()))
				.map(handle -> handle.handle(te, buf, ctx)).orElse(null);
	}
	
	static {
		INSTANCE.registerMessage(PacketUpdateTE::onMessage, PacketUpdateTE.class, 0, Side.CLIENT);
		INSTANCE.registerMessage(PacketRequestUpdateTELandiaTower::onMessage, PacketRequestUpdateTELandiaTower.class, 1, Side.SERVER);
	}
}
