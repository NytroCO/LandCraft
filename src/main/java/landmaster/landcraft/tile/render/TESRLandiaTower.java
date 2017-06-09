package landmaster.landcraft.tile.render;

import org.lwjgl.opengl.*;

import landmaster.landcraft.*;
import landmaster.landcraft.block.*;
import landmaster.landcraft.tile.*;
import landmaster.landcraft.util.*;
import net.minecraft.client.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.tileentity.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

public class TESRLandiaTower extends TileEntitySpecialRenderer<TELandiaTower> {
	public static final ResourceLocation LASER_LOC = new ResourceLocation(LandCraft.MODID, "textures/effects/laserbeam.png");
	
	@Override
	public void renderTileEntityAt(TELandiaTower te, double x, double y, double z, float partialTicks, int destroyStage) {
		if (te.getTargetEntity() != null) {
			GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
			
			GlStateManager.pushMatrix();
			
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			
			double doubleX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
            double doubleY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
            double doubleZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
			
			
			Vec3d vec = new Vec3d(doubleX, doubleY+player.getEyeHeight(), doubleZ);
			Vec3d vec0 = new Vec3d(te.getPos()).addVector(0.5, BlockLandiaTower.MAX_POSITION+1, 0.5);
			Vec3d vec1 = new Vec3d(te.getPos().getX()+0.5, te.getWorld().getHeight(), te.getPos().getZ()+0.5);
			
			GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
			
			Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();
            
            this.bindTexture(LASER_LOC);
            
            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
			
			ClientUtils.drawBeam(vec0, vec1, vec, 0.3f);
			
			tessellator.draw();
			
			GlStateManager.popMatrix();
			
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		}
	}
}
