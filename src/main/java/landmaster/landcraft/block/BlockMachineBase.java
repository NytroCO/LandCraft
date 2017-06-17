package landmaster.landcraft.block;

import landmaster.landcraft.*;
import landmaster.landcraft.gui.proxy.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class BlockMachineBase extends Block {
	public BlockMachineBase(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    public BlockMachineBase(Material materialIn) {
        super(materialIn);
    }
    
    @Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side,
            float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			player.openGui(LandCraft.INSTANCE, GuiProxy.GUI_TE_ID, world, pos.getX(), pos.getY(), pos.getZ());
        }
		return true;
	}
	
	public ItemStack getWrenchDrop(World world, BlockPos pos) {
		ItemStack is = new ItemStack(Item.getItemFromBlock(this));
		NBTTagCompound nbt = new NBTTagCompound();
		TileEntity te = world.getTileEntity(pos);
		if (te != null) {
			nbt.setTag("BlockEntityTag", te.writeToNBT(new NBTTagCompound()));
		}
		is.setTagCompound(nbt);
		return is;
	}
}
