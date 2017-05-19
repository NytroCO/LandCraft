package landmaster.landcraft.jei;

import java.util.function.*;

import mcjty.lib.jei.*;
import mezz.jei.api.recipe.*;

public class RecipeHandlerBase<T> extends CompatRecipeHandler<T> {
	private final Class<T> clazz;
	private final Function<T, IRecipeWrapper> func;
	
	public RecipeHandlerBase(Class<T> clazz, Function<T, IRecipeWrapper> func, String id) {
		super(id);
		this.clazz = clazz;
		this.func = func;
	}

	@Override
	public Class<T> getRecipeClass() {
		return clazz;
	}

	@Override
	public IRecipeWrapper getRecipeWrapper(T recipe) {
		return func.apply(recipe);
	}
}
