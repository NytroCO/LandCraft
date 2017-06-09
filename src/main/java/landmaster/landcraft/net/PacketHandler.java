package landmaster.landcraft.net;

import landmaster.landcraft.*;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.*;

public class PacketHandler {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(LandCraft.MODID);
	
	public static void init() {
		INSTANCE.registerMessage(PacketUpdateTEBreeder::onMessage, PacketUpdateTEBreeder.class, 0, Side.CLIENT);
		INSTANCE.registerMessage(PacketUpdateTEThoriumGenerator::onMessage, PacketUpdateTEThoriumGenerator.class, 1, Side.CLIENT);
		INSTANCE.registerMessage(PacketUpdateClientEnergy::onMessage, PacketUpdateClientEnergy.class, 2, Side.CLIENT);
		INSTANCE.registerMessage(PacketUpdateClientFluid::onMessage, PacketUpdateClientFluid.class, 3, Side.CLIENT);
		INSTANCE.registerMessage(PacketUpdateTEPot::onMessage, PacketUpdateTEPot.class, 4, Side.CLIENT);
		INSTANCE.registerMessage(PacketUpdateTELandiaTower::onMessage, PacketUpdateTELandiaTower.class, 5, Side.CLIENT);
	}
}
