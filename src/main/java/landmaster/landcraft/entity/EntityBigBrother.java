package landmaster.landcraft.entity;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import javax.annotation.*;

import com.google.common.collect.*;

import gnu.trove.set.hash.*;
import landmaster.landcore.entity.*;
import landmaster.landcraft.entity.ai.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.*;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.pathfinding.*;
import net.minecraft.potion.*;
import net.minecraft.server.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.*;

public class EntityBigBrother extends EntityMob {
	private final BossInfoServer bossInfo = (BossInfoServer) (new BossInfoServer(this.getDisplayName(),
			BossInfo.Color.BLUE, BossInfo.Overlay.PROGRESS).setDarkenSky(true));
	
	public static final ResourceLocation LOOT = new ResourceLocation("landcraft:entities/big_brother");
	
	private final Set<UUID> henchmen = new THashSet<>();
	private static final String HENCHMEN_NBT = "OrwellHenchmen";
	
	public static final float ATK_RANGE = 80.0f;
	
	static {
		MinecraftForge.EVENT_BUS.register(EntityBigBrother.class);
	}
	
	/**
	 * Prevent henchmen from targeting Big Brother.
	 * @param event the event
	 */
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
		this.setSize(0.6F*4, 1.95F*4.28f);
		this.setPathPriority(PathNodeType.LAVA, 8.0F);
		this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
		this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
		this.isImmuneToFire = true;
		this.experienceValue = 200;
	}
	
	@Override
	protected void despawnEntity() {} // entity is omnipresent (no, I'm kidding)
	
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
		return entityIn != null && (entityIn == this || (super.isOnSameTeam(entityIn) || this.fetchHenchmen().contains(entityIn)));
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
	
	/**
	 * Make the items dropped from this mob invulnerable.
	 * @param event the event
	 */
	@SubscribeEvent
	public static void onLivingDrops(LivingDropsEvent event) {
		if (event.getEntity() instanceof EntityBigBrother) {
			event.getDrops().forEach(item -> item.setEntityInvulnerable(true));
		}
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (this.rand.nextFloat() < 0.03f) {
			this.heal(1);
		}
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
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(210.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(9.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(20.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).setBaseValue(13.0D);
		this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(ATK_RANGE);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.6D);
	}
	
	private static final List<Function<World, EntityLiving>> HENCHMEN_LIST = ImmutableList.of(
			EntityWizard::new, EntityPigZombie::new, EntityLandlord::new, EntityWitherSkeleton::new);
	
	@Override
	protected void initEntityAI() {
		this.tasks.addTask(1, new EntityAISwimming(this));
		this.tasks.addTask(4, new EntityAISummonHenchmen(this,
				owner -> Stream.generate(() -> HENCHMEN_LIST
						.get(owner.getRNG().nextInt(HENCHMEN_LIST.size()))
						.apply(owner.getEntityWorld()))
				.limit(2 + owner.getRNG().nextInt(3)).iterator(), 0.02f, 5.0f));
		this.tasks.addTask(5, new EntityAIMoveTowardsTarget(this, 1.0, 80));
		this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 1.0D));
		this.tasks.addTask(7, new EntityAIWander(this, 1.0D));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
		this.tasks.addTask(8, new EntityAILookIdle(this));
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
		this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, false));
	}
	
	@Override
	public boolean isNonBoss() {
		return false;
	}
}
