package landmaster.landcraft.item;

import java.util.*;

import javax.annotation.*;

import landmaster.landcraft.config.*;
import landmaster.landcraft.content.*;
import landmaster.landcraft.world.save.*;
import net.minecraft.client.resources.*;
import net.minecraft.client.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

public class ItemWeatherWand extends Item {
	public ItemWeatherWand() {
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("weather_wand").setRegistryName("weather_wand");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		tooltip.add(I18n.format("tooltip.weather_wand.info"));
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		final ItemStack stack = playerIn.getHeldItem(hand);
		if (worldIn.isRemote || worldIn.provider.getDimension() != Config.landiaDimensionID) {
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
		final LandiaWeather instance = LandiaWeather.get(worldIn);
		instance.setClear(!instance.isClear());
		instance.markDirty();
		playerIn.sendMessage(new TextComponentTranslation("msg.weather_wand.use"));
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}
}
