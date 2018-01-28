package landmaster.landcraft.entity.ai;

import java.util.*;
import java.util.function.*;

import landmaster.landcraft.entity.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.util.text.*;

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
	public boolean shouldContinueExecuting() {
        return this.shouldExecute() || !this.owner.getNavigator().noPath() || countdown > 0;
    }
	
	@Override
	public void resetTask() {
		this.owner.fetchHenchmen().forEach(Entity::setDead);
	}
	
	@Override
	public void startExecuting() {
		countdown = 240 + owner.getRNG().nextInt(260);
		if (!owner.getEntityWorld().isRemote) {
			owner.getEntityWorld().playerEntities.stream()
			.filter(player -> owner.getDistanceSq(player) < EntityBigBrother.ATK_RANGE*EntityBigBrother.ATK_RANGE)
			.forEach(player -> player.sendMessage(new TextComponentTranslation("msg.big_brother.henchmen.summon")));
		}
	}
	
	@Override
	public void updateTask() {
		--countdown;
		final EntityLivingBase target = owner.getAttackTarget();
		if (owner.getRNG().nextFloat() < chance && target != null) {
			factory.apply(owner).forEachRemaining(entity -> {
				entity.setPosition(target.posX + owner.getRNG().nextFloat() * scatterSize - scatterSize / 2,
						target.posY, target.posZ + owner.getRNG().nextFloat() * scatterSize - scatterSize / 2);
				entity.setAttackTarget(target);
				owner.addHenchman(entity);
				owner.getEntityWorld().spawnEntity(entity);
			});
		}
	}
}
