package landmaster.landcraft.entity.ai;

import java.util.*;
import java.util.function.*;

import javax.annotation.*;

import landmaster.landcraft.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;

public class EntityAIRandomTarget extends EntityAITarget {
	private final float range;
	private final Predicate<EntityLivingBase> predicate;
	
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
		return this.taskOwner.getRNG().nextFloat() < 0.2f;
	}
	
	@Override
	public void startExecuting() {
		List<EntityLivingBase> targets = this.taskOwner.getEntityWorld()
				.getEntitiesWithinAABB(EntityLivingBase.class, Utils.AABBfromVecs(
						this.taskOwner.getPositionVector().subtract(range, range, range),
						this.taskOwner.getPositionVector().addVector(range, range, range)),
						predicate
						.and(Predicate.isEqual(taskOwner).negate())
						.and(entity -> entity.getDistanceSqToEntity(taskOwner) <= range*range)
						.and(entity -> this.isSuitableTarget(entity, false))
						::test);
		if (!targets.isEmpty()) {
			this.taskOwner.setAttackTarget(targets.get(this.taskOwner.getRNG().nextInt(targets.size())));
		}
		super.startExecuting();
	}
}
