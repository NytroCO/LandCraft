package landmaster.landcraft.jei;

import landmaster.landcraft.api.*;
import mezz.jei.api.recipe.*;

public abstract class PotRecipeJEI implements IRecipeWrapper {
	protected PotRecipes.RecipeProcess prc;
	
	public PotRecipeJEI(PotRecipes.RecipeProcess prc) {
		this.prc = prc;
	}
}
