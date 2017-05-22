package landmaster.landcraft.block;

import java.util.*;

import javax.annotation.*;

import landmaster.landcore.api.block.IMetaBlockName;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.creativetab.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraftforge.fml.relauncher.*;


public abstract class BlockModSlab<E extends Enum<E> & IStringSerializable> extends BlockSlab implements IMetaBlockName {
	
	public final PropertyEnum<E> prop;
	private final E[] values;
	
	private static PropertyEnum<?> tmp;
	
	public BlockModSlab(Material material, PropertyEnum<E> prop) {
		super(preInit(material, prop));
		this.prop = prop;
		values = prop.getValueClass().getEnumConstants();
		this.setDefaultState(
				this.blockState.getBaseState().withProperty(BlockSlab.HALF, EnumBlockHalf.BOTTOM));
		this.useNeighborBrightness = true;
	}
	
	private static Material preInit(Material material, PropertyEnum<?> property) {
		tmp = property;
		return material;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(@Nonnull Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
		this.func_149666_a(itemIn, tab, list);
	}
	
	public void func_149666_a(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		for (E type : values) {
			list.add(new ItemStack(this, 1, type.ordinal()));
		}
	}
	
	@Nonnull
	@Override
	protected BlockStateContainer createBlockState() {
		if (prop == null) {
			return new BlockStateContainer(this, HALF, tmp);
		}
		return new BlockStateContainer(this, HALF, prop);
	}
	
	/**
	 * Convert the given metadata into a BlockState for this Block
	 */
	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(prop, fromMeta(meta & 7)).withProperty(HALF,
				(meta & 8) == 0 ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
	}
	
	/**
	 * Convert the BlockState into the correct metadata value
	 */
	@Override
	public int getMetaFromState(IBlockState state) {
		int i = 0;
		i = i | state.getValue(prop).ordinal();
		
		if (state.getValue(HALF) == EnumBlockHalf.TOP) {
			i |= 8;
		}
		
		return i;
	}
	
	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(prop).ordinal();
	}
	
	protected E fromMeta(int meta) {
		if (meta < 0 || meta >= values.length) {
			meta = 0;
		}
		
		return values[meta];
	}
	
	@Nonnull
	@Override
	public IProperty<E> getVariantProperty() {
		return prop;
	}
	
	@Nonnull
	@Override
	public Comparable<?> getTypeForItem(@Nonnull ItemStack stack) {
		return fromMeta(stack.getItemDamage() & 7);
	}
	
	@Nonnull
	@Override
	public String getUnlocalizedName(int meta) {
		return super.getUnlocalizedName() + "." + fromMeta(meta & 7).getName();
	}
	
	@Override
	public String getSpecialName(ItemStack stack) {
		return fromMeta(stack.getItemDamage() & 7).getName();
	}
	
	/**
	 * Gets the full variant of the slab, as double slabs are not used here
	 *
	 * @param state
	 *            Input slab state, in most cases state.getValue() with a switch
	 *            is all you need
	 * @return An IBlockState of the full block
	 */
	public abstract IBlockState getFullBlock(IBlockState state);
	
	// all our slabs are single
	@Override
	public boolean isDouble() {
		return false;
	}
}
