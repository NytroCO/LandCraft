package landmaster.landcraft.api;

import java.util.*;

import javax.annotation.*;

import org.apache.commons.lang3.*;
import org.apache.commons.lang3.tuple.*;

import com.google.common.collect.*;

import gnu.trove.iterator.*;
import gnu.trove.list.*;
import gnu.trove.list.array.*;
import net.minecraft.item.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.oredict.*;

public class PotRecipes {
	/**
	 * An interface representing a recipe.
	 * @author Landmaster
	 */
	@FunctionalInterface
	public static interface RecipeProcess {
		/**
		 * Checks for a recipe match.
		 * @param in1 First input stack
		 * @param in2 Second input stack
		 * @param in3 Third input stack
		 * @param fs Input fluid
		 * @return A {@link RecipeOutput} describing the recipe
		 */
		RecipeOutput process(ItemStack in1, ItemStack in2, ItemStack in3, FluidStack fs);
	}
	
	public static class RecipePOredict implements RecipeProcess {
		public final String s1, s2, s3;
		public final FluidStack fs;
		public final RecipeOutput out;
		
		@Deprecated
		public RecipePOredict(int i1, int i2, int i3, FluidStack fs, RecipeOutput out) {
			this(OreDictionary.getOreName(i1), OreDictionary.getOreName(i2), OreDictionary.getOreName(i3), fs, out);
		}
		
		public RecipePOredict(String s1, String s2, String s3, FluidStack fs, RecipeOutput out) {
			this.s1 = s1;
			this.s2 = s2;
			this.s3 = s3;
			this.fs = fs;
			if (this.fs != null) this.fs.amount = out.fluidPerTick;
			this.out = out;
		}
		
		public static RecipePOredict of(String s1, String s2, String s3, FluidStack fs, RecipeOutput out) {
			return new RecipePOredict(s1, s2, s3, fs, out);
		}
		
		@Override
		public RecipeOutput process(ItemStack in1, ItemStack in2, ItemStack in3, FluidStack fs) {
			if (in1.isEmpty() || in2.isEmpty() || in3.isEmpty()) {
				return new RecipeOutput();
			}
			List<int[]> stacks_ods = Lists
					.transform(Arrays.asList(in1, in2, in3), OreDictionary::getOreIDs);
			TIntList required = TIntArrayList.wrap(new int[] {
					OreDictionary.getOreID(s1), OreDictionary.getOreID(s2), OreDictionary.getOreID(s3)}); // list never grows
			
			for (int[] stack_ods: stacks_ods) {
				boolean valid = false;
				TIntIterator req_it = required.iterator();
				while (req_it.hasNext()) {
					int next = req_it.next();
					if (ArrayUtils.contains(stack_ods, next)) {
						valid = true;
						req_it.remove();
						break;
					}
				}
				if (!valid) {
					return new RecipeOutput();
				}
			}
			if (!required.isEmpty() || !fs.equals(this.fs)) {
				return new RecipeOutput();
			}
			return out;
		}
	}
	
	/**
	 * Describes the recipe.
	 * @author Landmaster
	 */
	public static class RecipeOutput {
		public ItemStack out = ItemStack.EMPTY;
		public int fluidPerTick;
		public int energyPerTick;
		public int time;
		
		/**
		 * Creates a new {@link RecipeOutput}
		 * @param out Output stack
		 * @param fluidPerTick Fluid consumed per tick
		 * @param energyPerTick Energy consumed per tick
		 * @param time in ticks for the recipe to process
		 */
		public RecipeOutput(ItemStack out, int fluidPerTick, int energyPerTick, int time) {
			this.out = out;
			this.fluidPerTick = fluidPerTick;
			this.energyPerTick = energyPerTick;
			this.time = time;
		}
		/**
		 * Creates an empty {@link RecipeOutput}
		 */
		public RecipeOutput() {
			// nothing to do
		}
		
		@Override
		public String toString() {
			return String.format(Locale.US,
					"PotRecipes.RecipeOutput(out=%s, fluidPerTick=%s, energyPerTick=%s, time=%s)",
					out, fluidPerTick, energyPerTick, time);
		}
	}
	
	private static final List<RecipeProcess> recipes = new ArrayList<>();
	
	/**
	 * Add a recipe. This should be called on both the server and client.
	 * @param rp The recipe
	 */
	public static void addRecipe(RecipeProcess rp) {
		getRecipeList().add(rp);
	}
	
	/**
	 * Get the recipe list; modifications to it should be done on both the server and client.
	 * @return the recipe list
	 */
	public static List<RecipeProcess> getRecipeList() {
		return recipes;
	}
	
	@Nonnull
	public static Pair<RecipeProcess, RecipeOutput> findRecipe(ItemStack in1, ItemStack in2, ItemStack in3, FluidStack fs) {
		for (RecipeProcess rp: recipes) {
			try {
				RecipeOutput out = rp.process(in1, in2, in3, fs);
				if (!isEmptyROutput(out)) {
					return Pair.of(rp, out);
				}
			} catch (NullPointerException | IllegalArgumentException e) {
				// skip recipe
			}
		}
		return emptyRPair();
	}
	
	public static boolean isEmptyROutput(RecipeOutput out) {
		return out.out.isEmpty();
	}
	
	@Nonnull
	public static Pair<RecipeProcess, RecipeOutput> emptyRPair() {
		return Pair.of((in1_, in2_, in3_, fs_) -> new RecipeOutput(), new RecipeOutput());
	}
	
	public static boolean isEmptyRPair(Pair<RecipeProcess, RecipeOutput> pair) {
		return isEmptyROutput(pair.getRight());
	}
}
