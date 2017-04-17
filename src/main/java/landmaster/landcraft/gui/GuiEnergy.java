package landmaster.landcraft.gui;

import java.util.*;

import landmaster.landcraft.*;
import landmaster.landcraft.container.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.util.*;

public abstract class GuiEnergy extends GuiContainer {
	private static final ResourceLocation bar = new ResourceLocation(LandCraft.MODID, "textures/gui/energy_bar.png");
	
	private ContEnergy cont;
	protected int energy;
	
	public GuiEnergy(ContEnergy cont) {
		super(cont);
		this.cont = cont;
	}
	
	// Setters
	public void setEnergy(int energy) {
		this.energy = energy;
	}
	
	protected void drawBackBar(int xPos, int yPos) {
		mc.renderEngine.bindTexture(bar);
		
		double percentEmpty = ((double)(cont.getTE().getMaxEnergyStored(null)-energy)) / cont.getTE().getMaxEnergyStored(null);
		
		drawTexturedModalRect(xPos, yPos, 17, 0, 14, 42);
		drawTexturedModalRect(xPos, yPos, 1, 0, 14, (int)(42*percentEmpty));
	}
	
	protected void drawFrontBar(int xPos, int yPos, int mouseX, int mouseY) {
		int k = (this.width - this.xSize) / 2; //X asis on GUI
		int l = (this.height - this.ySize) / 2; //Y asis on GUI
		int trueX = mouseX-k, trueY = mouseY-l;
		
		if (isPointInRegion(xPos, yPos, 14, 42, mouseX, mouseY)) {
			drawHoveringText(Arrays.asList(String.format("%d RF / %d RF",
					energy,
					cont.getTE().getMaxEnergyStored(null))), trueX, trueY);
		}
	}
}
