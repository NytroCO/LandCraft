package landmaster.landcraft.block;

import landmaster.landcraft.content.LandCraftContent;
import landmaster.landcraft.tile.TEBreeder;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class BlockBreeder extends BlockMachineBase {
    public BlockBreeder() {
        super(Material.ROCK);
        this.setHarvestLevel("pickaxe", 0);
        this.setHardness(1.5F);
        this.setResistance(10.0F);
        this.setSoundType(SoundType.STONE);
        this.setUnlocalizedName("breeder").setRegistryName("breeder");
        this.setCreativeTab(LandCraftContent.creativeTab);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TEBreeder) {
            IItemHandler handler = tile.getCapability(
                    CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            for (int i = 0; i < handler.getSlots(); ++i) {
                ItemStack is = handler.getStackInSlot(i);
                if (!is.isEmpty()) {
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
    @ParametersAreNonnullByDefault
    public TEBreeder createTileEntity(World world, IBlockState state) {
        return new TEBreeder();
    }
}
