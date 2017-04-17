package landmaster.landcraft.net;

import landmaster.landcraft.*;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

public class PacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(LandCraft.MODID);
	
	public static void init() {
		int msgInd = 0;
		INSTANCE.registerMessage(PacketUpdateTEBreeder.class, PacketUpdateTEBreeder.class, msgInd++, Side.CLIENT);
		INSTANCE.registerMessage(PacketUpdateClientEnergy.class, PacketUpdateClientEnergy.class, msgInd++, Side.CLIENT);
	}
}
