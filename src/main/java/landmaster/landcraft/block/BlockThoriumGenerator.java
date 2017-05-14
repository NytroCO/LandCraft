package landmaster.landcraft.block;

import javax.annotation.*;

import landmaster.landcraft.content.*;
import landmaster.landcraft.tile.*;
import mcjty.lib.tools.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.state.*;
import net.minecraft.entity.item.*;
import net.minecraft.item.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.items.*;

public class BlockThoriumGenerator extends BlockMachineBase {
	public BlockThoriumGenerator() {
		super(Material.ROCK);
		this.setHarvestLevel("pickaxe", 0);
		this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setUnlocalizedName("thorium_generator").setRegistryName("thorium_generator");
        this.setCreativeTab(LandCraftContent.creativeTab);
	}
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TEThoriumGenerator) {
			IItemHandler handler = tile.getCapability(
					CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
			for (int i=0; i<handler.getSlots(); ++i) {
				ItemStack is = handler.getStackInSlot(i);
				if (!ItemStackTools.isEmpty(is)) {
					EntityItem ent = new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), is);
					world.spawnEntity(ent);
				}
			}
			super.breakBlock(world, pos, state);
		}
	}
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}
	@Nullable
	@Override
	public TEThoriumGenerator createTileEntity(World world, IBlockState state) {
		return new TEThoriumGenerator();
	}
}
