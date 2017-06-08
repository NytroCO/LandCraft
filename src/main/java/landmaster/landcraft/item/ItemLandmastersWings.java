package landmaster.landcraft.item;

import java.util.*;

import org.lwjgl.opengl.*;

import landmaster.landcore.api.item.*;
import landmaster.landcraft.*;
import landmaster.landcraft.content.*;
import landmaster.landcraft.util.*;
import mcjty.lib.tools.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.client.resources.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.relauncher.*;

public class ItemLandmastersWings extends ItemEnergyBase {
	public static final int ENERGY_PER_TICK = 100;
	public static final int MAX_ENERGY = 3000000;
	
	public ItemLandmastersWings() {
		super(MAX_ENERGY, MAX_ENERGY, MAX_ENERGY);
		this.maxStackSize = 1;
		this.setUnlocalizedName("landmasters_wings").setRegistryName("landmasters_wings");
		this.setCreativeTab(LandCraftContent.creativeTab);
		
		proxy.initEvents();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	protected void clGetSubItems(Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
		ItemStack empty = new ItemStack(itemIn);
		subItems.add(empty);
		ItemStack full = empty.copy();
		this.setEnergyStored(full, MAX_ENERGY);
		subItems.add(full);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack itemStack) {
		return true;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return (double) (getMaxEnergyStored(stack) - getEnergyStored(stack)) / getMaxEnergyStored(stack);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void clAddInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		tooltip.add(I18n.format("tooltip.landmasters_wings.info", ENERGY_PER_TICK));
		tooltip.add(TextFormatting.GREEN+String.format("%d RF / %d RF", getEnergyStored(stack), getMaxEnergyStored(stack)));
	}
	
	@Override
	public ActionResult<ItemStack> clOnItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand) {
		ItemStack stack = playerIn.getHeldItem(hand);
		
		if (worldIn.isRemote) {
			return new ActionResult<>(EnumActionResult.PASS, stack);
		}
		
		EntityEquipmentSlot slot = EntityLiving.getSlotForItemStack(stack);
		ItemStack old = playerIn.getItemStackFromSlot(slot);
		
		if (ItemStackTools.isEmpty(old)) { // vacant
			playerIn.setItemStackToSlot(slot, stack.copy());
			ItemStackTools.setStackSize(stack, 0);
			return new ActionResult<>(EnumActionResult.SUCCESS, stack);
		}
		
		return new ActionResult<>(EnumActionResult.FAIL, stack);
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		if (world.isRemote) return;
		if (!player.isCreative() && !player.isSpectator()
				&& player.getItemStackFromSlot(EntityLiving.getSlotForItemStack(itemStack)) == itemStack) {
			if (this.extractEnergy(itemStack, ENERGY_PER_TICK, true) >= ENERGY_PER_TICK) {
				player.capabilities.allowFlying = true;
				if (player.capabilities.isFlying) {
					this.extractEnergy(itemStack, ENERGY_PER_TICK, false);
				}
			} else {
				player.capabilities.allowFlying = false;
				player.capabilities.isFlying = false;
			}
			player.sendPlayerAbilities();
		}
		if (player.isCreative() || player.isSpectator()) {
			player.capabilities.allowFlying = true;
			player.sendPlayerAbilities();
		}
	}
	
	@SidedProxy(serverSide = "landmaster.landcraft.item.ItemLandmastersWings$Proxy", clientSide = "landmaster.landcraft.item.ItemLandmastersWings$ProxyClient")
	public static Proxy proxy;
	
	public static class Proxy {
		public void initEvents() {
		}
	}
	
	public static class ProxyClient extends Proxy {
		public static final ResourceLocation LASER_LOC = new ResourceLocation(LandCraft.MODID, "textures/effects/laserbeam.png");
		
		@Override
		public void initEvents() {
			MinecraftForge.EVENT_BUS.register(this);
		}
		
		@SubscribeEvent
		public void renderPlayerLasers(RenderPlayerEvent.Post event) {
			ItemStack stack = event.getEntityPlayer().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			if (stack != null && stack.getItem() instanceof ItemLandmastersWings && event.getEntityPlayer().capabilities.isFlying) {
				float partialTicks = event.getPartialRenderTick();
				
				float yaw = event.getEntity().prevRotationYaw
						+ (event.getEntity().rotationYaw - event.getEntity().prevRotationYaw) * partialTicks;
				float pitch = event.getEntity().prevRotationPitch
						+ (event.getEntity().rotationPitch - event.getEntity().prevRotationPitch) * partialTicks;
				
				GlStateManager.depthMask(false);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
				
				GlStateManager.pushMatrix();
				
				EntityPlayerSP player = Minecraft.getMinecraft().player;
				
				double doubleX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
				double doubleY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
				double doubleZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
				
				Vec3d vec = new Vec3d(doubleX, doubleY + player.getEyeHeight(), doubleZ);
				
				EntityPlayer player0 = event.getEntityPlayer();
				
				double double0X = player0.lastTickPosX + (player0.posX - player0.lastTickPosX) * partialTicks;
				double double0Y = player0.lastTickPosY + (player0.posY - player0.lastTickPosY) * partialTicks;
				double double0Z = player0.lastTickPosZ + (player0.posZ - player0.lastTickPosZ) * partialTicks;
				
				Vec3d vec0 = new Vec3d(double0X, double0Y + player0.height * 0.5, double0Z);
				
				GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
				
				Tessellator tessellator = Tessellator.getInstance();
	            VertexBuffer buffer = tessellator.getBuffer();
	            
	            Minecraft.getMinecraft().renderEngine.bindTexture(LASER_LOC);
	            
	            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
				
				for (int i = 0; i < 12; ++i) {
					Vec3d spoke = (new Vec3d(5 * MathHelper.cos((float) Math.PI * i / 6), 0,
							5 * MathHelper.sin((float) Math.PI * i / 6)))
									.rotatePitch((float) ((pitch + player.ticksExisted + partialTicks + (player.getUniqueID().hashCode() % 360)) * Math.PI / 180))
									.rotateYaw((float) (yaw * Math.PI / 180));
					ClientUtils.drawBeam(vec0, vec0.add(spoke), vec, 0.1f);
				}
				
				tessellator.draw();
				
				GlStateManager.popMatrix();
				
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}
		}
	}
}