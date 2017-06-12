package landmaster.landcraft.gui;

import java.text.*;
import java.util.*;

import landmaster.landcraft.*;
import landmaster.landcraft.container.*;
import landmaster.landcraft.tile.*;
import landmaster.landcraft.util.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.*;
import net.minecraft.util.*;
import net.minecraftforge.fluids.*;

public class GuiTEPot extends GuiEnergy {
	private static final ResourceLocation background = new ResourceLocation(LandCraft.MODID, "textures/gui/pot.png");
	
	private static final int fluid_x = 80, fluid_y = 5;
	private static final int energy_x = 150, energy_y = 5;
	
	private int fheight = 0;
	
	private ContTEPot cont;
	
	public GuiTEPot(ContTEPot cont) {
		super(cont);
		this.cont = cont;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.renderEngine.bindTexture(background);
		
		int energy = cont.getTE().getEnergyStored(null);
		FluidStack fs = ((TEPot)cont.getTE()).getFluid();
		
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		fheight = 0;
		if (fs != null) {
			TextureAtlasSprite ftex = mc.getTextureMapBlocks().getTextureExtry(
					fs.getFluid().getStill().toString());
			mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			fheight = fs.amount * 50 / 8000;
			drawTexturedModalRect(guiLeft+fluid_x, guiTop+fluid_y+(50-fheight), ftex, 64, fheight);
		}
		mc.renderEngine.bindTexture(background);
		drawTexturedModalRect(guiLeft+fluid_x, guiTop+fluid_y, 176, 0, 64, 50);
		this.drawBackBar(energy, guiLeft+energy_x, guiTop+energy_y);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		int k = (this.width - this.xSize) / 2; //X asis on GUI
		int l = (this.height - this.ySize) / 2; //Y asis on GUI
		int trueX = mouseX-k, trueY = mouseY-l;
		
		fontRenderer.drawString(I18n.format("tile.pot.name"), 8, 6, 0x404040);
		fontRenderer.drawString(cont.getPlayerInv().getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 0x404040);
		
		int energy = cont.getTE().getEnergyStored(null);
		int time = ((TEPot)cont.getTE()).getClientTime();
		int progress = ((TEPot)cont.getTE()).getProgress();
		FluidStack fs = ((TEPot)cont.getTE()).getFluid();
		
		if (time > 0) {
			NumberFormat nf = NumberFormat.getPercentInstance(Utils.getLocale());
			nf.setMaximumFractionDigits(1);
			fontRenderer.drawString(nf.format(
					((double)progress) / time),
					30, 39, 0x0000FF);
		}
		
		if (fs != null && isPointInRegion(fluid_x, fluid_y, 64, 50, mouseX, mouseY)) {
			drawHoveringText(Arrays.asList(fs.getLocalizedName(), fs.amount+"mB"), trueX, trueY);
		}
		
		this.drawFrontBar(energy, energy_x, energy_y, mouseX, mouseY);
	}
}
