package landmaster.landcraft.world.gen;

import java.util.*;

import net.minecraft.util.math.*;
import net.minecraft.world.*;

public interface ITreeGenerator {
	default void generateTree(Random random, World world, BlockPos pos) {}
}
