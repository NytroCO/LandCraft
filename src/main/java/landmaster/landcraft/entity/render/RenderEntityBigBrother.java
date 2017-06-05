package landmaster.landcraft.entity.render;

import java.util.*;

import org.lwjgl.opengl.*;

import landmaster.landcraft.*;
import landmaster.landcraft.entity.*;
import landmaster.landcraft.util.*;
import net.minecraft.client.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.vertex.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;

public class RenderEntityBigBrother extends RenderLiving<EntityBigBrother> {
	public static final ResourceLocation LASER_LOC = new ResourceLocation(LandCraft.MODID,
			"textures/effects/laserbeam.png");
	
	public static final ResourceLocation tex = new ResourceLocation(LandCraft.MODID, "textures/entity/big_brother.png");
	
	public RenderEntityBigBrother(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelVillager(0), 0.5f);
		this.addLayer(new LayerCustomHead(this.getMainModel().villagerHead));
	}
	
	@Override
	public ModelVillager getMainModel() {
		return (ModelVillager) super.getMainModel();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityBigBrother entity) {
		return tex;
	}
	
	@Override
	protected void preRenderCallback(EntityBigBrother entitylivingbaseIn, float partialTickTime) {
		GlStateManager.scale(4, 4, 4);
	}
	
	@Override
	public void doRender(EntityBigBrother entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
		if (entity.isLaserActive()) { // render laser
			Optional.ofNullable(Utils.raytraceEntityPlayerLook(entity, EntityBigBrother.ATK_RANGE))
					.filter(rtr -> !(rtr.entityHit instanceof EntityBigBrother))
					.ifPresent(rtr -> {
						GlStateManager.depthMask(false);
						GlStateManager.enableBlend();
						GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
						
						GlStateManager.pushMatrix();
						
						EntityPlayer player = Minecraft.getMinecraft().player;
						
						double doubleX = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
						double doubleY = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
						double doubleZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
						
						Vec3d vec = new Vec3d(doubleX, doubleY + player.getEyeHeight(), doubleZ);
						Vec3d vec0 = entity.getPositionVector().addVector(0, entity.getEyeHeight(), 0);
						Vec3d vec1 = rtr.hitVec;
						
						GlStateManager.translate(-doubleX, -doubleY, -doubleZ);
						
						Tessellator tessellator = Tessellator.getInstance();
						VertexBuffer buffer = tessellator.getBuffer();
						
						Minecraft.getMinecraft().renderEngine.bindTexture(LASER_LOC);
						
						buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
						
						ClientUtils.drawBeam(vec0, vec1, vec, 0.13f);
						
						tessellator.draw();
						
						GlStateManager.popMatrix();
						
						GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					});
		}
	}
}
