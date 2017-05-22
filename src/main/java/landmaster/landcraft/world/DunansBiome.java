package landmaster.landcraft.world;

import java.util.*;

import landmaster.landcraft.content.*;
import landmaster.landcraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

public class DunansBiome extends LandiaBiome {
	protected static final LandiaTreeGenerator TREE_GEN = new LandiaTreeGenerator(9, 7,
			LandCraftContent.landia_log.getDefaultState().withProperty(LandiaTreeType.L_TYPE, LandiaTreeType.CINNAMON),
			LandCraftContent.landia_leaves.getDefaultState().withProperty(LandiaTreeType.L_TYPE,
					LandiaTreeType.CINNAMON));
	
	public DunansBiome(BiomeProperties properties) {
		super(properties);
		this.theBiomeDecorator.treesPerChunk = 6;
		this.theBiomeDecorator.flowersPerChunk = 14;
		this.theBiomeDecorator.mushroomsPerChunk = 8;
		this.theBiomeDecorator.bigMushroomsPerChunk = 4;
		this.theBiomeDecorator.waterlilyPerChunk = 4;
	}
	
	@Override
	public void decorate(World worldIn, Random rand, BlockPos pos) {
		super.decorate(worldIn, rand, pos);
		
		int xSpawn = pos.getX() + 8 + rand.nextInt(16);
		int ySpawn = 117;
		int zSpawn = pos.getZ() + 8 + rand.nextInt(16);
		BlockPos position = new BlockPos(xSpawn, ySpawn, zSpawn);
		
		TREE_GEN.generateTree(rand, worldIn, position);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getSkyColorByTemp(float currentTemperature) {
		return 0x06074F;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getGrassColorAtPos(BlockPos pos) {
		return 0x000099;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getFoliageColorAtPos(BlockPos pos) {
		return 0x000099;
	}
}
