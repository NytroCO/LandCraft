package landmaster.landcraft.gui;

import landmaster.landcraft.*;
import landmaster.landcraft.container.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.resources.*;
import net.minecraft.util.*;

public class GuiTEBreeder extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation(LandCraft.MODID, "textures/gui/breeder.png");
	
	private ContTEBreeder cont;
	private int fuel;
	private double temperature;
	private int product;
	
	public GuiTEBreeder(ContTEBreeder cont) {
		super(cont);
		this.cont = cont;
	}
	
	// Setters
	public void setTemp(double arg) { temperature = arg; }
	public void setFuel(int arg) { fuel = arg; }
	public void setProduct(int arg) { product = arg; }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.renderEngine.bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		fontRendererObj.drawString(I18n.format("tile.breeder.name"), 8, 6, 0x404040);
		fontRendererObj.drawString(cont.getPlayerInv().getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 0x404040);
		fontRendererObj.drawString(String.format("F: %s; T: %s; P: %s",
				fuel, temperature, product), 8, 18, 0x0000FF);
	}
}
