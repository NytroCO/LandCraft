package landmaster.landcraft.block;

import java.util.*;

import javax.annotation.*;

import landmaster.landcore.api.block.*;
import landmaster.landcraft.*;
import landmaster.landcraft.util.*;
import landmaster.landcraft.world.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.terraingen.*;

public class BlockLandiaSapling extends BlockSapling implements IMetaBlockName {
	public BlockLandiaSapling() {
		this.setCreativeTab(LandCraft.creativeTab);
		this.setDefaultState(this.blockState.getBaseState());
		this.setSoundType(SoundType.PLANT);
		this.setHardness(0.0F);
		this.setUnlocalizedName("landia_sapling").setRegistryName("landia_sapling");
	}
	
	@Override
	public void getSubBlocks(@Nonnull Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		for (LandiaTreeType type : LandiaTreeType.values()) {
			list.add(new ItemStack(this, 1,
					this.getMetaFromState(this.getDefaultState().withProperty(LandiaTreeType.L_TYPE, type))));
		}
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		// TYPE has to be included because of the BlockSapling constructor.. but
		// it's never used.
		return new BlockStateContainer(this, LandiaTreeType.L_TYPE, STAGE, TYPE);
	}
	
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		if (meta < 0 || meta >= LandiaTreeType.values().length) {
			meta = 0;
		}
		
		LandiaTreeType sapling = LandiaTreeType.values()[meta];
		
		return this.getDefaultState().withProperty(LandiaTreeType.L_TYPE, sapling);
	}
	
	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(LandiaTreeType.L_TYPE).ordinal();
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state);
	}
	
	@Override
	public boolean isReplaceable(IBlockAccess worldIn, @Nonnull BlockPos pos) {
		return false;
	}
	
	@Nonnull
	@Override
	public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
		return EnumPlantType.Plains;
	}
	
	@Nonnull
	@Override
	public ItemStack getPickBlock(@Nonnull IBlockState state, RayTraceResult target, @Nonnull World world,
			@Nonnull BlockPos pos, EntityPlayer player) {
		IBlockState iblockstate = world.getBlockState(pos);
		int meta = iblockstate.getBlock().getMetaFromState(iblockstate);
		return new ItemStack(Item.getItemFromBlock(this), 1, meta);
	}
	
	@Override
	public void generateTree(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state,
			@Nonnull Random rand) {
		if (!TerrainGen.saplingGrowTree(worldIn, rand, pos)) {
			return;
		}
		BaseTreeGenerator gen = new BaseTreeGenerator();
		IBlockState log;
		IBlockState leaves;
		
		switch (state.getValue(LandiaTreeType.L_TYPE)) {
		case CINNAMON:
			log = LandCraft.landia_log.getDefaultState().withProperty(LandiaTreeType.L_TYPE, LandiaTreeType.CINNAMON);
			leaves = LandCraft.landia_leaves.getDefaultState().withProperty(LandiaTreeType.L_TYPE,
					LandiaTreeType.CINNAMON);
			
			gen = new LandiaTreeGenerator(9, 3, log, leaves, true, true);
			
			break;
		default:
			LandCraft.log.warn("BlockLandiaLog Warning: Invalid sapling meta/foliage, "
					+ state.getValue(LandiaTreeType.L_TYPE) + ". Please report!");
			break;
		}
		
		// replace sapling with air
		worldIn.setBlockToAir(pos);
		
		// try generating
		gen.generateTree(rand, worldIn, pos);
		
		// check if it generated
		if (worldIn.isAirBlock(pos)) {
			// nope, set sapling again
			worldIn.setBlockState(pos, state, 4);
		}
	}

	@Override
	public String getSpecialName(ItemStack stack) {
		return getStateFromMeta(stack.getMetadata()).getValue(LandiaTreeType.L_TYPE).toString();
	}
}