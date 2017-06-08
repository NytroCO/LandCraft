package landmaster.landcraft.entity.ai;

import java.util.*;
import java.util.function.*;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;

public class EntityAISummonHenchmen<T extends EntityLiving> extends EntityAIBase {
	protected final T owner;
	protected final Function<T, Iterator<? extends EntityLiving>> factory;
	protected final float chance;
	protected final float scatterSize;
	
	public EntityAISummonHenchmen(T owner, Function<T, Iterator<? extends EntityLiving>> factory, float chance, float scatterSize) {
		this.owner = owner;
		this.factory = factory;
		this.chance = chance;
		this.scatterSize = scatterSize;
		this.setMutexBits(3);
	}
	
	@Override
	public boolean shouldExecute() {
		return owner.getAttackTarget() != null;
	}
	
	@Override
	public void startExecuting() {
		
	}
	
	@Override
	public void updateTask() {
		if (owner.getRNG().nextFloat() < chance) {
			factory.apply(owner).forEachRemaining(entity -> {
				entity.setPosition(owner.posX + owner.getRNG().nextFloat() * scatterSize - scatterSize / 2,
						owner.posY, owner.posZ + owner.getRNG().nextFloat() * scatterSize - scatterSize / 2);
				entity.setAttackTarget(owner.getAttackTarget());
				owner.getEntityWorld().spawnEntity(entity);
			});
		}
	}
}
