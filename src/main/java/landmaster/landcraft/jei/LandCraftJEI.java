package landmaster.landcraft.jei;

import landmaster.landcraft.*;
import landmaster.landcraft.api.*;
import landmaster.landcraft.config.*;
import landmaster.landcraft.container.*;
import landmaster.landcraft.content.*;
import mezz.jei.api.*;
import mezz.jei.api.recipe.transfer.*;
import net.minecraft.item.*;

@JEIPlugin
public class LandCraftJEI extends BlankModPlugin {
	@Override
	public void register(IModRegistry registry) {
		LandCraft.log.debug("Adding JEI integration for LandCraft");
		
		//final IIngredientRegistry ingredientRegistry = registry.getIngredientRegistry();
		final IJeiHelpers jeiHelpers = registry.getJeiHelpers();
		final IGuiHelper guiHelper = jeiHelpers.getGuiHelper();
		final IRecipeTransferRegistry recipeTransferRegistry = registry.getRecipeTransferRegistry();
		
		if (Config.pot) {
			registry.addRecipeCategories(new PotRecipeCategory(guiHelper));
			registry.handleRecipes(PotRecipes.RecipePOredict.class, PotOredictRecipeJEI::new, PotRecipeCategory.UID);
			
			registry.addRecipes(PotRecipes.getRecipeList(), PotRecipeCategory.UID);
			
			recipeTransferRegistry.addRecipeTransferHandler(ContTEPot.class, PotRecipeCategory.UID, 0, 3, 4, 36);
			
			registry.addRecipeCategoryCraftingItem(new ItemStack(LandCraftContent.pot), PotRecipeCategory.UID);
		}
	}
}
