package landmaster.landcraft.item;

import javax.annotation.*;

import landmaster.landcore.api.item.*;
import landmaster.landcraft.block.*;
import mcjty.lib.tools.*;
import net.minecraft.block.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

public class ItemBlockSlab<T extends Enum<T> & IStringSerializable> extends ItemBlockMeta {
	public BlockModSlab<T> slab;
	
	public ItemBlockSlab(BlockModSlab<T> block) {
		super(block);
		this.slab = block;
	}
	
	/**
	 * Called when a Block is right-clicked with this Item
	 */
	@Nonnull
	@Override
	protected EnumActionResult clOnItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand,
			EnumFacing facing, float hitX, float hitY, float hitZ) {
		final ItemStack stack = player.getHeldItem(hand);
		// don't place the slab if unable to edit
		if (!ItemStackTools.isEmpty(stack) && player.canPlayerEdit(pos.offset(facing), facing, stack)) {
			
			// try placing the slab at the current position
			// note that this requires the slab to be extended on the side the
			// block was clicked
			if (tryPlace(player, stack, world, pos, facing)) {
				return EnumActionResult.SUCCESS;
			}
			// otherwise. try and place it in the block in front
			else if (this.tryPlace(player, stack, world, pos.offset(facing), null)) {
				return EnumActionResult.SUCCESS;
			}
			
			return super.clOnItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
		} else {
			return EnumActionResult.FAIL;
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean canPlaceBlockOnSide(World world, @Nonnull BlockPos pos, @Nonnull EnumFacing side,
			EntityPlayer player, @Nonnull ItemStack stack) {
		BlockPos oldPos = pos;
		Comparable<?> type = this.slab.getTypeForItem(stack);
		IBlockState state = world.getBlockState(pos);
		
		// first, try placing on the same block
		if (state.getBlock() == this.slab) {
			boolean flag = state.getValue(BlockSlab.HALF) == BlockSlab.EnumBlockHalf.TOP;
			
			if ((side == EnumFacing.UP && !flag || side == EnumFacing.DOWN && flag)
					&& type == state.getValue(this.slab.prop) && this.slab.getFullBlock(state) != null) {
				return true;
			}
		}
		
		// if that does not work, offset by one and try same type
		pos = pos.offset(side);
		state = world.getBlockState(pos);
		return state.getBlock() == this.slab && type == state.getValue(this.slab.prop)
				|| super.canPlaceBlockOnSide(world, oldPos, side, player, stack);
	}
	
	private boolean tryPlace(EntityPlayer player, ItemStack stack, World worldIn, BlockPos pos, EnumFacing facing) {
		IBlockState state = worldIn.getBlockState(pos);
		Comparable<?> type = this.slab.getTypeForItem(stack);
		
		if (state.getBlock() == this.slab) {
			BlockSlab.EnumBlockHalf half = state.getValue(BlockSlab.HALF);
			
			if (type == state.getValue(this.slab.prop)
					&& (facing == null || facing == EnumFacing.UP && half == BlockSlab.EnumBlockHalf.BOTTOM
							|| facing == EnumFacing.DOWN && half == BlockSlab.EnumBlockHalf.TOP)) {
				
				IBlockState fullBlock = this.slab.getFullBlock(state);
				if (fullBlock != null) {
					AxisAlignedBB axisalignedbb = fullBlock.getCollisionBoundingBox(worldIn, pos);
					
					if (axisalignedbb != Block.NULL_AABB && worldIn.checkNoEntityCollision(axisalignedbb.offset(pos))
							&& worldIn.setBlockState(pos, fullBlock, 11)) {
						SoundType soundtype = fullBlock.getBlock().getSoundType(fullBlock, worldIn, pos, player);
						worldIn.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS,
								(soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
						ItemStackTools.incStackSize(stack, -1);
					}
					
					return true;
				}
			}
		}
		
		return false;
	}
}
