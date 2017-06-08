package landmaster.landcraft.entity;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import javax.annotation.*;

import com.google.common.collect.*;

import gnu.trove.set.hash.*;
import landmaster.landcore.entity.*;
import landmaster.landcraft.entity.ai.*;
import landmaster.landcraft.util.*;
import mcjty.lib.tools.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.network.datasync.*;
import net.minecraft.pathfinding.*;
import net.minecraft.potion.*;
import net.minecraft.server.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.*;

public class EntityBigBrother extends EntityMob implements IRangedAttackMob {
	private final BossInfoServer bossInfo = (BossInfoServer) (new BossInfoServer(this.getDisplayName(),
			BossInfo.Color.BLUE, BossInfo.Overlay.PROGRESS).setDarkenSky(true));
	
	public static final ResourceLocation LOOT = new ResourceLocation("landcraft:entities/big_brother");
	
	private static final DataParameter<Integer> LASER_DURATION = EntityDataManager.createKey(EntityBigBrother.class, DataSerializers.VARINT);
	
	private final Set<UUID> henchmen = new THashSet<>();
	private static final String HENCHMEN_NBT = "OrwellHenchmen";
	
	public static final float ATK_RANGE = 80.0f;
	
	private static final int MAX_LASER_DURATION = 10;
	
	static {
		MinecraftForge.EVENT_BUS.register(EntityBigBrother.class);
	}
	
	@SubscribeEvent
	public static void attackTarget(LivingSetAttackTargetEvent event) {
		if (event.getTarget() instanceof EntityBigBrother
				&& ((EntityBigBrother)event.getTarget()).getAttackTarget() != null
				&& ((EntityBigBrother)event.getTarget()).fetchHenchmen().contains(event.getEntity())
				&& event.getEntity() instanceof EntityLiving
				&& !((EntityLiving)event.getEntity()).getAttackTarget().equals(((EntityBigBrother)event.getTarget()).getAttackTarget())) {
			((EntityLiving)event.getEntity()).setAttackTarget(((EntityBigBrother)event.getTarget()).getAttackTarget());
		}
	}
	
	public EntityBigBrother(World worldIn) {
		super(worldIn);
		this.setSize(0.6F*4, 1.95F*4);
		this.setPathPriority(PathNodeType.LAVA, 8.0F);
		this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
		this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
		this.isImmuneToFire = true;
		this.experienceValue = 200;
	}
	
	public Set<Entity> fetchHenchmen() {
		purgeHenchmen();
		final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return henchmen.stream()
				.map(server::getEntityFromUuid)
				.collect(Collectors.toCollection(THashSet::new));
	}
	
	private void purgeHenchmen() {
		final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		henchmen.removeIf(uuid -> server.getEntityFromUuid(uuid) == null);
	}
	
	public void addHenchman(@Nonnull Entity entity) {
		henchmen.add(entity.getUniqueID());
	}
	
	@Override
	public void readEntityFromNBT(NBTTagCompound compound) {
		super.readEntityFromNBT(compound);
		final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		if (this.hasCustomName()) {
			this.bossInfo.setName(this.getDisplayName());
		}
		int[] int_arr = compound.getIntArray(HENCHMEN_NBT);
		for (int i=0; i<int_arr.length; i+=4) {
			UUID uuid = new UUID((((long)int_arr[i]) << 32) | (int_arr[i+1] & 0xFFFFFFFFL),
					(((long)int_arr[i+2]) << 32) | (int_arr[i+3] & 0xFFFFFFFFL));
			if (server.getEntityFromUuid(uuid) != null) henchmen.add(uuid);
		}
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound compound) {
		super.writeEntityToNBT(compound);
		final MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		int[] int_arr = new int[henchmen.size()*4];
		int i = 0;
		for (UUID uuid: henchmen) {
			if (server.getEntityFromUuid(uuid) != null) {
				int_arr[i] = (int)(uuid.getMostSignificantBits() >> 32);
				int_arr[i+1] = (int)uuid.getMostSignificantBits();
				int_arr[i+2] = (int)(uuid.getLeastSignificantBits() >> 32);
				int_arr[i+3] = (int)uuid.getLeastSignificantBits();
				i += 4;
			}
		}
		compound.setIntArray(HENCHMEN_NBT, int_arr);
	}
	
	@Override
	public boolean isOnSameTeam(Entity entityIn) {
		return entityIn == null ? false : (entityIn == this ? true : (super.isOnSameTeam(entityIn) ? true : this.fetchHenchmen().contains(entityIn)));
	}
	
	@Override
	public boolean isImmuneToExplosions() {
		return true;
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
		Optional.ofNullable(Utils.raytraceEntity(this, this.getPositionVector().add(this.getLookVec().scale(2.8)).addVector(0, this.getEyeHeight(), 0), this.getLookVec(), ATK_RANGE, true))
				.map(rtr -> rtr.entityHit)
				.map(entity -> { System.out.println("ENTITY CLASS: " + entity.getClass()); return entity; }) // TODO remove log
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
	
	private static final List<Function<World, EntityLiving>> HENCHMEN_LIST = ImmutableList.of(
			EntityWizard::new, EntityPigZombie::new, EntityLandlord::new,
			world -> EntityTools.createEntity(world, "WitherSkeleton"),
			EntityEnderman::new);
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(4, new EntityAISummonHenchmen(this, owner -> {
			return Stream.generate(() -> HENCHMEN_LIST
					.get(owner.getRNG().nextInt(HENCHMEN_LIST.size()))
					.apply(owner.getEntityWorld()))
			.limit(2 + owner.getRNG().nextInt(3))
			.iterator();
		}, 0.02f, 74.0f));
		this.tasks.addTask(4, new EntityAIAttackRanged(this, 0.5f, MAX_LASER_DURATION, ATK_RANGE));
		this.tasks.addTask(5, new EntityAIMoveTowardsTarget(this, 1.0, 80));
		this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false));
		this.targetTasks.addTask(3, new EntityAIRandomTarget(this, ATK_RANGE));
	}
	
	@Override
	public boolean isNonBoss() {
		return false;
	}
}
