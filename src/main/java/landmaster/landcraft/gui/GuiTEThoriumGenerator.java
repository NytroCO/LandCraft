package landmaster.landcraft.gui;

import java.text.*;
import java.util.*;

import landmaster.landcraft.api.*;
import landmaster.landcraft.container.*;
import landmaster.landcraft.tile.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.client.resources.*;
import net.minecraft.util.*;
import net.minecraftforge.fluids.*;

public class GuiTEThoriumGenerator extends GuiEnergy {
	private static final ResourceLocation background = new ResourceLocation(ModInfo.MODID, "textures/gui/thorium_generator.png");
	
	private int fheight = 0;
	
	private ContTEThoriumGenerator cont;
	
	public GuiTEThoriumGenerator(ContTEThoriumGenerator cont) {
		super(cont);
		this.cont = cont;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.renderEngine.bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		fheight = 0;
		
		FluidStack fs = ((TEThoriumGenerator)cont.getTE()).getFluid();
		
		if (fs != null) {
			TextureAtlasSprite ftex = mc.getTextureMapBlocks().getTextureExtry(
					fs.getFluid().getStill().toString());
			mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			fheight = fs.amount * 50 / 8000;
			drawTexturedModalRect(guiLeft+60, guiTop+15+(50-fheight), ftex, 64, fheight);
		}
		mc.renderEngine.bindTexture(background);
		drawTexturedModalRect(guiLeft+60, guiTop+15, 176, 0, 64, 50);
		
		int energy = cont.getTE().getEnergyStored(null);
		
		this.drawBackBar(energy, guiLeft+130, guiTop+15);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		int k = (this.width - this.xSize) / 2; //X asis on GUI
		int l = (this.height - this.ySize) / 2; //Y asis on GUI
		int trueX = mouseX-k, trueY = mouseY-l;
		
		int progress = ((TEThoriumGenerator)cont.getTE()).getProgress();
		FluidStack fs = ((TEThoriumGenerator)cont.getTE()).getFluid();
		
		fontRenderer.drawString(I18n.format("tile.thorium_generator.name"), 8, 6, 0x404040);
		fontRenderer.drawString(cont.getPlayerInv().getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 0x404040);
		
		if (progress >= 0) {
			NumberFormat nf = NumberFormat.getPercentInstance(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getJavaLocale());
			nf.setMaximumFractionDigits(1);
			fontRenderer.drawString(nf.format(
					((double)progress) / TEThoriumGenerator.THORIUM_BURN_TIME),
					30, 54, 0x0000FF);
		}
		
		if (fs != null && isPointInRegion(60, 15, 64, 50, mouseX, mouseY)) {
			drawHoveringText(Arrays.asList(fs.getLocalizedName(), fs.amount+"mB"), trueX, trueY);
		}
		
		int energy = cont.getTE().getEnergyStored(null);
		
		this.drawFrontBar(energy, 130, 15, mouseX, mouseY);
	}
}
