package landmaster.landcraft.gui;

import landmaster.landcraft.*;
import landmaster.landcraft.container.*;
import net.minecraft.client.resources.*;
import net.minecraft.util.*;

public class GuiTEPlayerMime extends GuiEnergy {
	private static final ResourceLocation background = new ResourceLocation(LandCraft.MODID, "textures/gui/player_mime.png");
	
	private ContTEPlayerMime cont;
	
	public GuiTEPlayerMime(ContTEPlayerMime cont) {
		super(cont);
		this.cont = cont;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.renderEngine.bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		
		int energy = cont.getTE().getEnergyStored(null);
		
		drawBackBar(energy, guiLeft+130, guiTop+14);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRenderer.drawString(I18n.format("tile.player_mime.name"), 8, 6, 0x404040);
		fontRenderer.drawString(cont.getPlayerInv().getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 0x404040);
		
		int energy = cont.getTE().getEnergyStored(null);
		
		drawFrontBar(energy, 130, 14, mouseX, mouseY);
	}
}
