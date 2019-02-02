package landmaster.landcraft.gui;

import java.util.*;

import landmaster.landcraft.api.ModInfo;
import landmaster.landcraft.container.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.util.*;

abstract class GuiEnergy extends GuiContainer {
	private static final ResourceLocation bar = new ResourceLocation(ModInfo.MODID, "textures/gui/energy_bar.png");
	
	private final ContEnergy cont;
	
	GuiEnergy(ContEnergy cont) {
		super(cont);
		this.cont = cont;
	}
	
	void drawBackBar(int energy, int xPos, int yPos) {
		mc.renderEngine.bindTexture(bar);
		
		double percentEmpty = ((double)(cont.getTE().getMaxEnergyStored(null)-energy)) / cont.getTE().getMaxEnergyStored(null);
		
		drawTexturedModalRect(xPos, yPos, 17, 0, 14, 42);
		drawTexturedModalRect(xPos, yPos, 1, 0, 14, (int)(42*percentEmpty));
	}
	
	void drawFrontBar(int energy, int xPos, int yPos, int mouseX, int mouseY) {
		int k = (this.width - this.xSize) / 2; //X asis on GUI
		int l = (this.height - this.ySize) / 2; //Y asis on GUI
		int trueX = mouseX-k, trueY = mouseY-l;
		
		if (isPointInRegion(xPos, yPos, 14, 42, mouseX, mouseY)) {
			drawHoveringText(Collections.singletonList(String.format("%d RF / %d RF",
					energy,
					cont.getTE().getMaxEnergyStored(null))), trueX, trueY);
		}
	}
}
