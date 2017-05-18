package landmaster.landcraft.jei;

import java.util.*;

import landmaster.landcraft.api.*;
import landmaster.landcraft.util.*;
import mezz.jei.api.ingredients.*;
import mezz.jei.api.recipe.*;
import net.minecraft.client.*;
import net.minecraft.client.resources.*;
import net.minecraft.item.*;

public class BreederFeedstockJEI extends BlankRecipeWrapper {
	private BreederFeedstock.OreMassTempTri omt;
	
	public BreederFeedstockJEI(BreederFeedstock.OreMassTempTri omt) {
		this.omt = omt;
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		minecraft.fontRenderer.drawSplitString(I18n.format("info.breeder.jei.temp", omt.temp), 40, 10, 120, 0);
		minecraft.fontRenderer.drawSplitString(I18n.format("info.breeder.jei.product", omt.mass), 40, 42, 120, 0);
	}
	
	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class, Arrays.asList(Utils.getOres(omt.oid)));
	}
	
}
