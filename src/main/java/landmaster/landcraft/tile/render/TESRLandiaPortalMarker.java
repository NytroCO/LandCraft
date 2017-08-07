package landmaster.landcraft.tile.render;

import org.lwjgl.opengl.*;

import landmaster.landcraft.api.*;
import landmaster.landcraft.block.*;
import landmaster.landcraft.tile.*;
import landmaster.landcraft.util.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

public class TESRLandiaPortalMarker extends TileEntitySpecialRenderer<TELandiaPortalMarker> {
	public static final ResourceLocation LASER_LOC = new ResourceLocation(ModInfo.MODID, "textures/effects/laserbeam.png");
	
	@Override
	public void renderTileEntityAt(TELandiaPortalMarker te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
		if (te.getWorld().getBlockState(te.getPos()).getValue(BlockLandiaPortalMarker.ACTIVATED)) {
			GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
			
			GlStateManager.pushMatrix();
			
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			
			double doubleX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
            double doubleY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
            double doubleZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
			
			
			Vec3d vec = new Vec3d(doubleX, doubleY+player.getEyeHeight(), doubleZ);
			Vec3d vec0 = new Vec3d(te.getPos()).addVector(0.5, -0.01, 0.5);
			Vec3d vec1 = new Vec3d(te.getPos().getX()+0.5, 0, te.getPos().getZ()+0.5);
			RayTraceResult rtr = te.getWorld().rayTraceBlocks(vec0, vec1);
			if (rtr != null) vec1 = rtr.hitVec;
			
			GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
			
			Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            
            this.bindTexture(LASER_LOC);
            
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			
			ClientUtils.drawBeam(buffer, vec0, vec1, vec, 0.3f);
			
			tessellator.draw();
			
			GlStateManager.popMatrix();
			
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}
}
