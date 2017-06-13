package landmaster.landcraft.item;

import java.util.List;

import landmaster.landcraft.config.*;
import landmaster.landcraft.content.*;
import landmaster.landcraft.world.save.*;
import mcjty.lib.compat.*;
import mcjty.lib.tools.*;
import net.minecraft.client.resources.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

public class ItemWeatherWand extends CompatItem {
	public ItemWeatherWand() {
		this.setCreativeTab(LandCraftContent.creativeTab);
		this.setUnlocalizedName("weather_wand").setRegistryName("weather_wand");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void clAddInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add(I18n.format("tooltip.weather_wand.info"));
	}
	
	@Override
	public ActionResult<ItemStack> clOnItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		final ItemStack stack = playerIn.getHeldItem(hand);
		if (worldIn.isRemote || worldIn.provider.getDimension() != Config.landiaDimensionID) {
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
		final LandiaWeather instance = LandiaWeather.get(worldIn);
		instance.setClear(!instance.isClear());
		instance.markDirty();
		ChatTools.addChatMessage(playerIn, new TextComponentTranslation("msg.weather_wand.use"));
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}
}
