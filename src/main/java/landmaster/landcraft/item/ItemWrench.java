package landmaster.landcraft.item;

import cofh.api.item.*;
import landmaster.landcraft.block.*;
import landmaster.landcraft.content.*;
import mcjty.lib.compat.*;
import mcjty.lib.tools.*;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.item.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class ItemWrench extends CompatItem implements IToolHammer {
	public ItemWrench() {
		this.setUnlocalizedName("item_wrench").setRegistryName("item_wrench");
		this.setCreativeTab(LandCraftContent.creativeTab);
	}
	
	@Override
	protected EnumActionResult clOnItemUse(EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.isRemote) return EnumActionResult.PASS;
		if (playerIn.isSneaking()) {
			Block block = worldIn.getBlockState(pos).getBlock();
			if (block instanceof BlockMachineBase) {
				ItemStack is = ((BlockMachineBase)block).getWrenchDrop(worldIn, pos);
				worldIn.destroyBlock(pos, false);
				EntityItem entItem = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), is);
				WorldTools.spawnEntity(worldIn, entItem);
				return EnumActionResult.SUCCESS;
			}
			return EnumActionResult.FAIL;
		} else {
			return EnumActionResult.PASS;
		}
	}

	@Override
	public boolean isUsable(ItemStack item, EntityLivingBase user, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isUsable(ItemStack item, EntityLivingBase user, Entity entity) {
		return true;
	}

	@Override
	public void toolUsed(ItemStack item, EntityLivingBase user, BlockPos pos) {
		// nothing
	}

	@Override
	public void toolUsed(ItemStack item, EntityLivingBase user, Entity entity) {
		// nothing
	}
	
}
