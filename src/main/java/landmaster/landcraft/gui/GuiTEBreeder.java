package landmaster.landcraft.gui;

import java.text.*;

import landmaster.landcraft.api.ModInfo;
import landmaster.landcraft.container.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.resources.*;
import net.minecraft.util.*;

public class GuiTEBreeder extends GuiContainer {
	private static final ResourceLocation background = new ResourceLocation(ModInfo.MODID, "textures/gui/breeder.png");
	
	private final ContTEBreeder cont;
	
	public GuiTEBreeder(ContTEBreeder cont) {
		super(cont);
		this.cont = cont;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.renderEngine.bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		int fuel = cont.getTE().getFuel(), product = cont.getTE().getProduct();
		double temperature = cont.getTE().getTemp();
		
		fontRenderer.drawString(I18n.format("tile.breeder.name"), 8, 6, 0x404040);
		fontRenderer.drawString(cont.getPlayerInv().getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 0x404040);
		fontRenderer.drawString(I18n.format("info.breeder.fuel",
				fuel), 15, 50, 0x00FF00);
		NumberFormat nf = NumberFormat.getNumberInstance(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getJavaLocale());
		nf.setMaximumFractionDigits(1);
		fontRenderer.drawString(I18n.format("info.breeder.temp",
				nf.format(temperature), nf.format(cont.getTE().maxTemp())), 15, 60, 0xFF0000);
		fontRenderer.drawString(I18n.format("info.breeder.product",
				product), 90, 50, 0x0000FF);
	}
}
