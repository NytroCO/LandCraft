package landmaster.landcraft.jei;

import landmaster.landcraft.api.ModInfo;
import mezz.jei.api.*;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.*;
import mezz.jei.api.recipe.*;
import net.minecraft.client.resources.*;
import net.minecraft.util.*;
import net.minecraft.util.text.*;

class PotRecipeCategory implements IRecipeCategory<PotRecipeJEI> {
	private static final ResourceLocation background_rl = new ResourceLocation(ModInfo.MODID, "textures/gui/pot.png");
	
	private final IDrawable background;
	private final IDrawable fluidOverlay;
	
	public static final String UID = "landcraft.pot";
	
	PotRecipeCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(background_rl, 4, 4, 160, 72);
		fluidOverlay = guiHelper.createDrawable(background_rl, 176, 0, 64, 50);
	}
	
	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public String getTitle() {
		return I18n.format("tile.pot.name");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}
	
	@Override
	public void setRecipe(IRecipeLayout recipeLayout, PotRecipeJEI recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup igroup = recipeLayout.getItemStacks();
		IGuiFluidStackGroup fgroup = recipeLayout.getFluidStacks();
		
		igroup.init(0, true, 10-4-1, 20-4-1);
		igroup.init(1, true, 30-4-1, 20-4-1);
		igroup.init(2, true, 50-4-1, 20-4-1);
		
		igroup.init(3, false, 30-4-1, 50-4-1);
		
		igroup.set(ingredients);
		
		
		fgroup.init(0, true, 80, 5, 64, 50, 40, false, fluidOverlay);
		fgroup.addTooltipCallback((idx, isIn, fs, tooltip) -> tooltip.add(TextFormatting.GOLD.toString()+TextFormatting.BOLD
				+I18n.format("tooltip.per_tick")));
		
		fgroup.set(ingredients);
	}

	@Override
	public String getModName() {
		return ModInfo.NAME;
	}
}
