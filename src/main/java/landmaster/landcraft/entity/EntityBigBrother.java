package landmaster.landcraft.entity;

import java.util.*;

import javax.annotation.*;

import landmaster.landcraft.entity.ai.*;
import landmaster.landcraft.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.network.datasync.*;
import net.minecraft.pathfinding.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class EntityBigBrother extends EntityMob implements IRangedAttackMob {
	private final BossInfoServer bossInfo = (BossInfoServer) (new BossInfoServer(this.getDisplayName(),
			BossInfo.Color.BLUE, BossInfo.Overlay.PROGRESS).setDarkenSky(true));
	
	public static final ResourceLocation LOOT = new ResourceLocation("landcraft:entities/big_brother");
	
	private static final DataParameter<Integer> LASER_DURATION = EntityDataManager.createKey(EntityBigBrother.class, DataSerializers.VARINT);
	
	public static final float ATK_RANGE = 80.0f;
	
	private static final int MAX_LASER_DURATION = 10;
	
	public EntityBigBrother(World worldIn) {
		super(worldIn);
		this.setSize(0.6F*4, 1.95F*4);
		this.setPathPriority(PathNodeType.LAVA, 8.0F);
		this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
		this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
		this.isImmuneToFire = true;
		this.experienceValue = 16;
	}
	
	@Override
	@Nullable
    protected ResourceLocation getLootTable() {
		return LOOT;
	}
	
	@Override
    protected void entityInit() {
		super.entityInit();
		this.getDataManager().register(LASER_DURATION, 0);
	}
	
	public void activateLaserDuration() {
		this.getDataManager().set(LASER_DURATION, MAX_LASER_DURATION * 1 / 2);
	}
	
	public boolean isLaserActive() {
		return this.getDataManager().get(LASER_DURATION) > 0;
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		int newdur = MathHelper.clamp(this.getDataManager().get(LASER_DURATION) - 1, 0, Integer.MAX_VALUE);
		this.getDataManager().set(LASER_DURATION, newdur);
	}
	
	@Override
	public void addPotionEffect(PotionEffect potioneffectIn) {
		// immune
	}
	
	@Override
	public boolean canAttackClass(Class<? extends EntityLivingBase> clazz) {
		return true; // can attack ghasts
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		if (this.hasCustomName()) {
			this.bossInfo.setName(this.getDisplayName());
		}
	}
	
	@Override
	public void setCustomNameTag(String name) {
		super.setCustomNameTag(name);
		this.bossInfo.setName(this.getDisplayName());
	}
	
	@Override
	protected void updateAITasks() {
		super.updateAITasks();
		this.bossInfo.setPercent(this.getHealth() / this.getMaxHealth());
	}
	
	@Override
	public void addTrackingPlayer(EntityPlayerMP player) {
		super.addTrackingPlayer(player);
		this.bossInfo.addPlayer(player);
	}
	
	@Override
	public void removeTrackingPlayer(EntityPlayerMP player) {
		super.removeTrackingPlayer(player);
		this.bossInfo.removePlayer(player);
	}
	
	@Override
	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		this.getLookHelper().setLookPosition(target.posX, target.posY, target.posZ, this.getHorizontalFaceSpeed(),
				this.getVerticalFaceSpeed());
		Optional.ofNullable(Utils.raytraceEntityPlayerLook(this, ATK_RANGE))
				.map(rtr -> rtr.entityHit)
				.filter(entity -> !(entity instanceof EntityBigBrother))
				.ifPresent(this::attackEntityAsMob);
		this.activateLaserDuration();
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(210.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(9.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(20.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(13.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(80.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.2D);
	}
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(4, new EntityAIAttackRanged(this, 0.5f, MAX_LASER_DURATION, ATK_RANGE));
		this.tasks.addTask(5, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(2, new EntityAIRandomTarget(this, ATK_RANGE));
	}
	
	@Override
	public boolean isNonBoss() {
		return false;
	}
}
