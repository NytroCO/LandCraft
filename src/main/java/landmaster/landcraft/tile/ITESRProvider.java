package landmaster.landcraft.tile;

import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.tileentity.*;
import net.minecraftforge.fml.relauncher.*;

public interface ITESRProvider<T extends TileEntity> {
	@SideOnly(Side.CLIENT)
	TileEntitySpecialRenderer<? super T> getRenderer();
}
