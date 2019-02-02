package landmaster.landcraft.jei;

import landmaster.landcraft.api.*;
import landmaster.landcraft.config.*;
import landmaster.landcraft.container.*;
import landmaster.landcraft.content.*;
import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.transfer.*;
import net.minecraft.item.*;

@JEIPlugin
class LandCraftJEI implements IModPlugin {
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		final IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		final IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		if (Config.breeder) {
			registry.addRecipeCategories(new BreederFeedstockCategory(guiHelper));
		}
		if (Config.pot) {
			registry.addRecipeCategories(new PotRecipeCategory(guiHelper));
		}
	}
	
	@Override
	public void register(IModRegistry registry) {
		LCLog.log.debug("Adding JEI integration for LandCraft");
		
		//final IIngredientRegistry ingredientRegistry = registry.getIngredientRegistry();
		//final IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		//final IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		final IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
		
		if (Config.breeder) {
			registry.handleRecipes(BreederFeedstock.OreMassTempTri.class, BreederFeedstockJEI::new, BreederFeedstockCategory.UID);
			registry.addRecipes(BreederFeedstock.getOreMassTempTris(), BreederFeedstockCategory.UID);
			
			recipeTransferRegistry.addRecipeTransferHandler(ContTEBreeder.class, BreederFeedstockCategory.UID, 0, 1, 3, 36);
			
			registry.addRecipeCatalyst(new ItemStack(LandCraftContent.breeder), BreederFeedstockCategory.UID);
		}
		
		if (Config.pot) {
			registry.handleRecipes(PotRecipes.RecipePOredict.class, PotOredictRecipeJEI::new, PotRecipeCategory.UID);
			
			registry.addRecipes(PotRecipes.getRecipeList(), PotRecipeCategory.UID);
			
			recipeTransferRegistry.addRecipeTransferHandler(ContTEPot.class, PotRecipeCategory.UID, 0, 3, 4, 36);
			
			registry.addRecipeCatalyst(new ItemStack(LandCraftContent.pot), PotRecipeCategory.UID);
		}
	}
}
