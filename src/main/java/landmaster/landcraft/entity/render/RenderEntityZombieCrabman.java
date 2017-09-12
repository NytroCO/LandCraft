package landmaster.landcraft.entity.render;

import landmaster.landcraft.api.*;
import landmaster.landcraft.entity.*;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.util.*;

public class RenderEntityZombieCrabman extends RenderBiped<EntityZombieCrabman> {
	public static final ResourceLocation tex = new ResourceLocation(ModInfo.MODID, "textures/entity/zombie_crabman.png");
	
	public RenderEntityZombieCrabman(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelZombie(), 0.5F);
		this.addLayer(new LayerBipedArmor(this)
        {
            protected void initArmor()
            {
                this.modelLeggings = new ModelZombie(0.5F, true);
                this.modelArmor = new ModelZombie(1.0F, true);
            }
        });
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityZombieCrabman entity) {
		return tex;
	}
}
