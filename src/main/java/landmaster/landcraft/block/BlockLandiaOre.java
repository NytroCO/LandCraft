package landmaster.landcraft.block;

import java.util.*;

import landmaster.landcore.api.block.*;
import landmaster.landcraft.*;
import landmaster.landcraft.util.*;
import mcjty.lib.compat.*;
import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

public class BlockLandiaOre extends CompatBlock implements IMetaBlockName {
	public static final PropertyEnum<LandiaOreType> TYPE = PropertyEnum.create("type", LandiaOreType.class);
	
	public BlockLandiaOre() {
		super(Material.ROCK);
		setDefaultState(blockState.getBaseState().withProperty(TYPE, LandiaOreType.KELLINE));
		setHardness(3f);
		setResistance(5f);
		for (LandiaOreType type: LandiaOreType.values()) {
			setHarvestLevel("pickaxe", type.getLevel(), getDefaultState().withProperty(TYPE, type));
		}
		this.setUnlocalizedName("landia_ore").setRegistryName("landia_ore");
		this.setCreativeTab(LandCraft.creativeTab);
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
	
	@SideOnly(Side.CLIENT)
	@Override
	protected void clGetSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		for (int i=0; i<LandiaOreType.values().length; ++i) {
			list.add(new ItemStack(itemIn, 1, i));
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
