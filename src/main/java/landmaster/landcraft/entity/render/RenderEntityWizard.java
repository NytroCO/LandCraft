package landmaster.landcraft.entity.render;

import landmaster.landcraft.api.ModInfo;
import landmaster.landcraft.entity.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.util.*;

public class RenderEntityWizard extends RenderLiving<EntityWizard> {
	private static final ResourceLocation tex = new ResourceLocation(ModInfo.MODID, "textures/entity/wizard.png");

	public RenderEntityWizard(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelVillager(0), 0.5f);
		this.addLayer(new LayerCustomHead(this.getMainModel().villagerHead));
	}
	
	@Override
	public ModelVillager getMainModel() {
        return (ModelVillager)super.getMainModel();
    }

	@Override
	protected ResourceLocation getEntityTexture(EntityWizard entity) {
		return tex;
	}
	
	@Override
	protected void preRenderCallback(EntityWizard entitylivingbaseIn, float partialTickTime) {
		GlStateManager.scale(1, 1.3f, 1);
	}
}
