package landmaster.landcraft.entity;

import javax.annotation.*;

import landmaster.landcraft.api.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.init.*;
import net.minecraft.pathfinding.*;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.*;

public class EntityWizard extends EntityMob implements IRangedAttackMob {
	public static final ResourceLocation LOOT = new ResourceLocation(ModInfo.MODID, "entities/wizard");
	
	public EntityWizard(World worldIn) {
		super(worldIn);
		this.setSize(0.6f, 2.7f);
		this.setPathPriority(PathNodeType.LAVA, 8.0F);
        this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
		this.isImmuneToFire = true;
		this.experienceValue = 13;
	}
	
	@Override
	public boolean isImmuneToExplosions() {
		return true;
	}
	
	@Override
	public boolean canAttackClass(Class<? extends EntityLivingBase> clazz) {
		return true; // can attack ghasts
	}
	
	@Override
	protected float applyPotionDamageCalculations(DamageSource source, float damage) {
        damage = super.applyPotionDamageCalculations(source, damage);

        if (source.getTrueSource() == this)
        {
            damage = 0.0F;
        }

        if (source.isMagicDamage())
        {
            damage = (float)((double)damage * 0.15D);
        }

        return damage;
    }
	
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_BLAZE_AMBIENT;
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(34.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(8.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(18.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(3.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(80.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
	}
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(4, new EntityAIAttackRanged(this, 0.5f, 10, 80));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPigZombie.class, true));
	}
	
	@Override
	@Nullable
    protected ResourceLocation getLootTable() {
		return LOOT;
	}
	
	@Override
    public int getMaxSpawnedInChunk() {
		return 3;
	}
	
	@Override
	public void onStruckByLightning(EntityLightningBolt lightningBolt) {
		// nothing to do
	}

	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		float rv = rand.nextFloat();
		if (rv < 0.8f) {
			double d0 = this.getDistanceSq(target);
			double d1 = target.posX - this.posX;
	        double d2 = target.getEntityBoundingBox().minY + (double)(this.height / 2.0F) - (this.posY + (double)(this.height / 2.0F));
	        double d3 = target.posZ - this.posZ;
	        float f = MathHelper.sqrt(MathHelper.sqrt(d0)) * 0.2F;
	        
			EntityFireball ball = new EntityWizardMagicFireball(world, this, d1 + this.getRNG().nextGaussian() * (double)f, d2, d3 + this.getRNG().nextGaussian() * (double)f);
			ball.posY = this.posY + (double)(this.height / 2.0F) + 0.5D;
			this.world.spawnEntity(ball);
		} else {
			this.world.addWeatherEffect(new EntityLightningBolt(world, target.posX, target.posY, target.posZ, false));
		}
	}

	@Override
	public void setSwingingArms(boolean swingingArms) {
		// no-op
	}
}
