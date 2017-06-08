package landmaster.landcraft.entity.ai;

import java.util.*;
import java.util.function.*;

import javax.annotation.*;

import landmaster.landcraft.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;

public class EntityAIRandomTarget extends EntityAITarget {
	protected final float range;
	protected final Predicate<EntityLivingBase> predicate;
	private int countdown;
	
	public EntityAIRandomTarget(EntityCreature entity, float range) {
		this(entity, range, e -> true);
	}
	
	public EntityAIRandomTarget(EntityCreature entity, float range, @Nonnull Predicate<EntityLivingBase> predicate) {
		super(entity, false);
		this.range = range;
		this.predicate = predicate;
		this.setMutexBits(1);
	}
	
	@Override
	public boolean shouldExecute() {
		return this.taskOwner.getRNG().nextFloat() < 0.3f;
	}
	
	@Override
	public boolean continueExecuting() {
		return countdown > 0;
	}
	
	@Override
	public void resetTask() {
		countdown = 0;
	}
	
	@Override
	public void updateTask() {
		--countdown;
	}
	
	@Override
	public void startExecuting() {
		countdown = 100+this.taskOwner.getRNG().nextInt(100);
		List<EntityLivingBase> targets = this.taskOwner.getEntityWorld()
				.getEntitiesWithinAABB(EntityLivingBase.class, Utils.AABBfromVecs(
						this.taskOwner.getPositionVector().subtract(range, range, range),
						this.taskOwner.getPositionVector().addVector(range, range, range)),
						predicate
						.and(entity -> entity != taskOwner
						&& entity.getDistanceSqToEntity(taskOwner) <= range*range
						&& this.isSuitableTarget(entity, false))
						::test);
		if (!targets.isEmpty()) {
			this.taskOwner.setAttackTarget(targets.get(this.taskOwner.getRNG().nextInt(targets.size())));
		}
		super.startExecuting();
	}
}
