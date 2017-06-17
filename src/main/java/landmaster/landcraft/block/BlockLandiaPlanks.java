package landmaster.landcraft.block;

import landmaster.landcore.api.block.*;
import landmaster.landcraft.content.*;
import landmaster.landcraft.util.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.state.*;
import net.minecraft.creativetab.*;
import net.minecraft.entity.player.*;
import net.minecraft.init.*;
import net.minecraft.item.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

public class BlockLandiaPlanks extends Block implements IMetaBlockName {
	public BlockLandiaPlanks() {
		super(Material.WOOD);
		Blocks.FIRE.setFireInfo(this, 5, 20);
		
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setHardness(2.0f);
		this.setSoundType(SoundType.WOOD);
		
		this.setUnlocalizedName("landia_planks").setRegistryName("landia_planks");
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
	    return new BlockStateContainer(this, LandiaTreeType.L_TYPE);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(LandiaTreeType.L_TYPE, LandiaTreeType.values()[meta]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		LandiaTreeType type = (LandiaTreeType)state.getValue(LandiaTreeType.L_TYPE);
		return type.ordinal();
	}
	
	@Override
	public int damageDropped(IBlockState state) {
	    return getMetaFromState(state);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(CreativeTabs tab, NonNullList<ItemStack> list) {
		for (int i=0; i<LandiaTreeType.values().length; ++i) {
			list.add(new ItemStack(this, 1, i));
		}
	}
	
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		return new ItemStack(Item.getItemFromBlock(this), 1, getMetaFromState(state));
	}
	
	@Override
	public String getSpecialName(ItemStack stack) {
		return getStateFromMeta(stack.getMetadata()).getValue(LandiaTreeType.L_TYPE).toString();
	}
}
