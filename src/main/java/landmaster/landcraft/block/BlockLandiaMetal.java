package landmaster.landcraft.block;

import landmaster.landcore.api.block.*;
import landmaster.landcraft.content.*;
import landmaster.landcraft.util.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class BlockLandiaMetal extends Block implements IMetaBlockName {
	public static final PropertyEnum<LandiaOreType> TYPE = PropertyEnum.create("type", LandiaOreType.class);
	
	public BlockLandiaMetal() {
		super(Material.ROCK);
		setDefaultState(blockState.getBaseState().withProperty(TYPE, LandiaOreType.KELLINE));
		setHardness(5f);
		setHarvestLevel("pickaxe", -1);
		this.setUnlocalizedName("landia_metal").setRegistryName("landia_metal");
		this.setCreativeTab(LandCraftContent.creativeTab);
	}
	
	@Override
	public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon) {
		return true;
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
	    return new BlockStateContainer(this, TYPE);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(TYPE, LandiaOreType.values()[meta]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		LandiaOreType type = (LandiaOreType)state.getValue(TYPE);
		return type.ordinal();
	}
	
	@Override
	public int damageDropped(IBlockState state) {
	    return getMetaFromState(state);
	}
	
	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (int i=0; i<LandiaOreType.values().length; ++i) {
			list.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public String getSpecialName(ItemStack stack) {
		return LandiaOreType.values()[stack.getMetadata()].toString();
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
	}
}
