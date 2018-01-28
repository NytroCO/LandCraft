package landmaster.landcore.entity;

import javax.annotation.*;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.datasync.*;
import net.minecraft.pathfinding.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.relauncher.*;

public class EntityLandlord extends EntityMob {
	/** Random offset used in floating behaviour */
    private float heightOffset = 0.5F;
    /** ticks until heightOffset is randomized */
    private int heightOffsetUpdateTime;
	
	private static final DataParameter<Boolean> CHARGED = EntityDataManager.createKey(
			EntityLandlord.class, DataSerializers.BOOLEAN);
	
	public static final ResourceLocation LOOT = new ResourceLocation("landcore:entities/landlord");
	
	public EntityLandlord(World worldIn) {
		super(worldIn);
		this.setPathPriority(PathNodeType.LAVA, 8.0F);
        this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
		this.isImmuneToFire = true;
		this.experienceValue = 10;
	}
	
	@Override
    protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(CHARGED, false);
	}
	
	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_BLAZE_AMBIENT;
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(28.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(7.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(10.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(2.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(80.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getBrightnessForRender() {
		return 0xF000F0;
	}
	
	@Override
	public void onLivingUpdate() {
        if (!this.onGround && this.motionY < 0.0D)
        {
            this.motionY *= 0.6D;
        }
        
        super.onLivingUpdate();
    }
	
	@Override
	public void fall(float distance, float damageMultiplier) {
    }
	
	@Override
	protected void updateAITasks()
    {
        --this.heightOffsetUpdateTime;

        if (this.heightOffsetUpdateTime <= 0)
        {
            this.heightOffsetUpdateTime = 100;
            this.heightOffset = 0.5F + (float)this.rand.nextGaussian() * 3.0F;
        }

        EntityLivingBase entitylivingbase = this.getAttackTarget();

        if (entitylivingbase != null && entitylivingbase.posY + (double)entitylivingbase.getEyeHeight() > this.posY + (double)this.getEyeHeight() + (double)this.heightOffset)
        {
            this.motionY += (0.30000001192092896D - this.motionY) * 0.30000001192092896D;
            this.isAirBorne = true;
        }

        super.updateAITasks();
    }
	
	public void setCharged(boolean val) {
		this.getDataManager().set(CHARGED, val);
	}
	
	public boolean isCharged() {
		return this.getDataManager().get(CHARGED);
	}
	
	@Override
	public boolean isBurning() {
        return this.isCharged();
    }
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(4, new AIFireballAttack(this));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
	}
	
	static class AIFireballAttack extends EntityAIBase
    {
        private final EntityLandlord landlord;
        private int attackStep;
        private int attackTime;

        public AIFireballAttack(EntityLandlord blazeIn)
        {
            this.landlord = blazeIn;
            this.setMutexBits(3);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute()
        {
            EntityLivingBase entitylivingbase = this.landlord.getAttackTarget();
            return entitylivingbase != null && entitylivingbase.isEntityAlive();
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting()
        {
            this.attackStep = 0;
        }

        /**
         * Resets the task
         */
        public void resetTask()
        {
            this.landlord.setCharged(false);
        }

        /**
         * Updates the task
         */
        public void updateTask()
        {
            --this.attackTime;
            EntityLivingBase entitylivingbase = this.landlord.getAttackTarget();
            double d0 = this.landlord.getDistanceSq(entitylivingbase);

            if (d0 < 4.0D)
            {
                if (this.attackTime <= 0)
                {
                    this.attackTime = 20;
                    this.landlord.attackEntityAsMob(entitylivingbase);
                }

                this.landlord.getMoveHelper().setMoveTo(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, 1.0D);
            }
            else if (d0 < 256.0D)
            {
                double d1 = entitylivingbase.posX - this.landlord.posX;
                double d2 = entitylivingbase.getEntityBoundingBox().minY + (double)(entitylivingbase.height / 2.0F) - (this.landlord.posY + (double)(this.landlord.height / 2.0F));
                double d3 = entitylivingbase.posZ - this.landlord.posZ;

                if (this.attackTime <= 0)
                {
                    ++this.attackStep;

                    if (this.attackStep == 1)
                    {
                        this.attackTime = 60;
                        this.landlord.setCharged(true);
                    }
                    else if (this.attackStep <= 4)
                    {
                        this.attackTime = 6;
                    }
                    else
                    {
                        this.attackTime = 100;
                        this.attackStep = 0;
                        this.landlord.setCharged(false);
                    }

                    if (this.attackStep > 1)
                    {
                        float f = MathHelper.sqrt(MathHelper.sqrt(d0)) * 0.5F;
                        this.landlord.getEntityWorld().playEvent((EntityPlayer)null, 1018, new BlockPos((int)this.landlord.posX, (int)this.landlord.posY, (int)this.landlord.posZ), 0);

                        for (int i = 0; i < 1; ++i)
                        {
                        	EntityFireball fireball = new EntityLandlordMagicFireball(this.landlord.getEntityWorld(), this.landlord, d1 + this.landlord.getRNG().nextGaussian() * (double)f, d2, d3 + this.landlord.getRNG().nextGaussian() * (double)f);
                            fireball.posY = this.landlord.posY + (double)(this.landlord.height / 2.0F) + 0.5D;
                            this.landlord.getEntityWorld().spawnEntity(fireball);
                        }
                    }
                }

                this.landlord.getLookHelper().setLookPositionWithEntity(entitylivingbase, 10.0F, 10.0F);
            }
            else
            {
                this.landlord.getNavigator().clearPath();
                this.landlord.getMoveHelper().setMoveTo(entitylivingbase.posX, entitylivingbase.posY, entitylivingbase.posZ, 1.0D);
            }

            super.updateTask();
        }
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
}
