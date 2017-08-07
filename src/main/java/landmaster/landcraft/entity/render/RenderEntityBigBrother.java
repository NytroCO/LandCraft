package landmaster.landcraft.entity.render;

import landmaster.landcraft.api.*;
import landmaster.landcraft.entity.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.util.*;

public class RenderEntityBigBrother extends RenderLiving<EntityBigBrother> {
	public static final ResourceLocation LASER_LOC = new ResourceLocation(ModInfo.MODID,
			"textures/effects/laserbeam.png");
	
	public static final ResourceLocation tex = new ResourceLocation(ModInfo.MODID, "textures/entity/big_brother.png");
	
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
}
