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
	/**
	 * Gui ID for opening tile entity GUIs.
	 */
	public static final int GUI_TE_ID = 0;
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case GUI_TE_ID:
			BlockPos pos = new BlockPos(x,y,z);
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TEBreeder) {
				return new ContTEBreeder(player, (TEBreeder)te);
			} else if (te instanceof TEPlayerMime) {
				return new ContTEPlayerMime(player, (TEPlayerMime)te);
			} else if (te instanceof TEThoriumGenerator) {
				return new ContTEThoriumGenerator(player, (TEThoriumGenerator)te);
			} else if (te instanceof TEPot) {
				return new ContTEPot(player, (TEPot)te);
			}
			break;
		}
		return null;
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch (ID) {
		case GUI_TE_ID:
			BlockPos pos = new BlockPos(x,y,z);
			TileEntity te = world.getTileEntity(pos);
			if (te instanceof TEBreeder) {
				return new GuiTEBreeder(new ContTEBreeder(player, (TEBreeder)te));
			} else if (te instanceof TEPlayerMime) {
				return new GuiTEPlayerMime(new ContTEPlayerMime(player, (TEPlayerMime)te));
			} else if (te instanceof TEThoriumGenerator) {
				return new GuiTEThoriumGenerator(new ContTEThoriumGenerator(player, (TEThoriumGenerator)te));
			} else if (te instanceof TEPot) {
				return new GuiTEPot(new ContTEPot(player, (TEPot)te));
			}
			break;
		}
		return null;
	}
}
