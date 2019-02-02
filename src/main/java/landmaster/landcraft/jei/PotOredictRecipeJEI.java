package landmaster.landcraft.jei;

import java.util.*;

import landmaster.landcraft.api.*;
import mezz.jei.api.ingredients.*;
import net.minecraft.client.*;
import net.minecraft.item.*;
import net.minecraft.util.text.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.oredict.*;

class PotOredictRecipeJEI extends PotRecipeJEI {
	private final PotRecipes.RecipePOredict prc;
	
	public PotOredictRecipeJEI(PotRecipes.RecipePOredict prc) {
		super(prc);
		this.prc = prc;
	}
	
	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		minecraft.fontRenderer.drawString(TextFormatting.DARK_RED.toString()+TextFormatting.BOLD
				+String.format(Locale.US, "%d RF/t", prc.out.energyPerTick),
				100, 58, 0x000000);
	}
	
	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInputLists(ItemStack.class,
				Arrays.asList(
						OreDictionary.getOres(prc.s1),
						OreDictionary.getOres(prc.s2),
						OreDictionary.getOres(prc.s3)
						));
		ingredients.setInput(FluidStack.class, prc.fs);
		ingredients.setOutput(ItemStack.class, prc.out.out);
	}
}
