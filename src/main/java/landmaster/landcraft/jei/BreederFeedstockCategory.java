package landmaster.landcraft.jei;

import landmaster.landcraft.*;
import mezz.jei.api.*;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.*;
import mezz.jei.api.recipe.*;
import net.minecraft.client.resources.*;
import net.minecraft.util.*;

public class BreederFeedstockCategory extends BlankRecipeCategory<BreederFeedstockJEI> {
	public static final ResourceLocation background_rl = new ResourceLocation(LandCraft.MODID, "textures/jei/breeder_feedstock.png");
	
	private final IDrawable background;
	
	public static final String UID = "landcraft.breeder.feedstock";
	
	BreederFeedstockCategory(IGuiHelper guiHelper) {
		background = guiHelper.createDrawable(background_rl, 0, 0, 160, 72);
	}
	
	@Override
	public String getUid() {
		return UID;
	}

	@Override
	public String getTitle() {
		return I18n.format("gui.header.breeder.feedstock");
	}

	@Override
	public IDrawable getBackground() {
		return background;
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, BreederFeedstockJEI recipeWrapper, IIngredients ingredients) {
		IGuiItemStackGroup igroup = recipeLayout.getItemStacks();
		
		igroup.init(0, true, 20-1, 20-1);
		
		igroup.set(ingredients);
	}
}
