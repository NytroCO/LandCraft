package landmaster.landcraft.jei;

import landmaster.landcraft.api.*;
import mezz.jei.api.recipe.*;

abstract class PotRecipeJEI implements IRecipeWrapper {
	private final PotRecipes.RecipeProcess prc;
	
	PotRecipeJEI(PotRecipes.RecipeProcess prc) {
		this.prc = prc;
	}
}
