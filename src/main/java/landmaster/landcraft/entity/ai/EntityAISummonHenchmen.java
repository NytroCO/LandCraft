package landmaster.landcraft.entity.ai;

import java.util.*;
import java.util.function.*;

import landmaster.landcraft.entity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;

public class EntityAISummonHenchmen extends EntityAIBase {
	protected final EntityBigBrother owner;
	protected final Function<EntityBigBrother, Iterator<? extends EntityLiving>> factory;
	protected final float chance;
	protected final float scatterSize;
	
	private int countdown;
	
	public EntityAISummonHenchmen(EntityBigBrother owner, Function<EntityBigBrother, Iterator<? extends EntityLiving>> factory, float chance, float scatterSize) {
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
	public boolean continueExecuting() {
        return this.shouldExecute() || !this.owner.getNavigator().noPath() || countdown > 0;
    }
	
	@Override
	public void resetTask() {
		this.owner.fetchHenchmen().forEach(Entity::setDead);
	}
	
	@Override
	public void startExecuting() {
		countdown = 200 + owner.getRNG().nextInt(200);
	}
	
	@Override
	public void updateTask() {
		--countdown;
		final EntityLivingBase target = owner.getAttackTarget();
		if (owner.getRNG().nextFloat() < chance && target != null) {
			factory.apply(owner).forEachRemaining(entity -> {
				entity.setPosition(target.posX + target.getRNG().nextFloat() * scatterSize - scatterSize / 2,
						target.posY, target.posZ + owner.getRNG().nextFloat() * scatterSize - scatterSize / 2);
				entity.setAttackTarget(owner.getAttackTarget());
				owner.addHenchman(entity);
				owner.getEntityWorld().spawnEntity(entity);
			});
		}
	}
}
