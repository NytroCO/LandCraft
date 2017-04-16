package landmaster.landcraft.gui.proxy;

import landmaster.landcraft.container.*;
import landmaster.landcraft.gui.*;
import landmaster.landcraft.tile.*;
import net.minecraft.entity.player.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.network.*;

public class GuiProxy implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x,y,z);
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TEBreeder) {
			return new ContTEBreeder(player, (TEBreeder)te);
		}
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x,y,z);
		TileEntity te = world.getTileEntity(pos);
		if (te instanceof TEBreeder) {
			return new GuiTEBreeder(new ContTEBreeder(player, (TEBreeder)te));
		}
		return null;
	}
}
