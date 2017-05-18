package landmaster.landcraft.api;

import java.util.*;

import javax.annotation.*;

import org.apache.commons.lang3.*;
import org.apache.commons.lang3.tuple.*;

import com.google.common.collect.*;

import gnu.trove.iterator.*;
import gnu.trove.list.*;
import gnu.trove.list.array.*;
import mcjty.lib.tools.*;
import net.minecraft.item.*;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.*;
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
		public final int i1, i2, i3;
		public final FluidStack fs;
		public final RecipeOutput out;
		
		public RecipePOredict(int i1, int i2, int i3, FluidStack fs, RecipeOutput out) {
			this.i1 = i1;
			this.i2 = i2;
			this.i3 = i3;
			this.fs = fs;
			if (this.fs != null) this.fs.amount = out.fluidPerTick;
			this.out = out;
		}
		
		public static RecipePOredict of(String s1, String s2, String s3, FluidStack fs, RecipeOutput out) {
			if (!OreDictionary.doesOreNameExist(s1)
					|| !OreDictionary.doesOreNameExist(s2)
					|| !OreDictionary.doesOreNameExist(s3)) {
				FMLLog.warning(String.format(Locale.US, "One of the ore names (%s, %s, %s) does not exist, skipping", s1, s2, s3));
				return new RecipePOredict(-1, -1, -1, null, new RecipeOutput());
			}
			return new RecipePOredict(OreDictionary.getOreID(s1), OreDictionary.getOreID(s2), OreDictionary.getOreID(s3), fs, out);
		}
		
		@Override
		public RecipeOutput process(ItemStack in1, ItemStack in2, ItemStack in3, FluidStack fs) {
			if (ItemStackTools.isEmpty(in1) || ItemStackTools.isEmpty(in2) || ItemStackTools.isEmpty(in3)) {
				return new RecipeOutput();
			}
			List<int[]> stacks_ods = Lists
					.transform(Arrays.asList(in1, in2, in3), OreDictionary::getOreIDs);
			TIntList required = TIntArrayList.wrap(new int[] {i1, i2, i3}); // list never grows
			
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
		public ItemStack out = ItemStackTools.getEmptyStack();
		public int fluidPerTick;
		public int energyPerTick;
		public int time;
		
		/**
		 * Creates a new {@link RecipeOutput}
		 * @param out Output stack
		 * @param in1N Amount of items to take from first input stack
		 * @param in2N Amount of items to take from second input stack
		 * @param in3N Amount of items to take from third input stack
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
		return ItemStackTools.isEmpty(out.out);
	}
	
	@Nonnull
	public static Pair<RecipeProcess, RecipeOutput> emptyRPair() {
		return Pair.of((in1_, in2_, in3_, fs_) -> new RecipeOutput(), new RecipeOutput());
	}
	
	public static boolean isEmptyRPair(Pair<RecipeProcess, RecipeOutput> pair) {
		return isEmptyROutput(pair.getRight());
	}
}