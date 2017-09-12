package landmaster.landcraft.entity;

import javax.annotation.Nullable;

import landmaster.landcraft.api.*;
import landmaster.landcraft.config.*;
import landmaster.landcraft.content.*;
import landmaster.landcraft.util.*;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.pathfinding.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.eventhandler.*;

@Mod.EventBusSubscriber(modid = ModInfo.MODID)
public class EntityZombieCrabman extends EntityZombie {
	public static final ResourceLocation LOOT = new ResourceLocation(ModInfo.MODID, "entities/zombie_crabman");
	
	public EntityZombieCrabman(World worldIn) {
		super(worldIn);
		this.setPathPriority(PathNodeType.LAVA, 8.0F);
        this.setPathPriority(PathNodeType.DANGER_FIRE, 0.0F);
        this.setPathPriority(PathNodeType.DAMAGE_FIRE, 0.0F);
		this.isImmuneToFire = true;
		this.experienceValue = 13;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		if (source.isMagicDamage() || source.isProjectile()) {
			return false;
		}
		return super.attackEntityFrom(source, amount);
	}
	
	@Override
	public boolean isImmuneToExplosions() {
		return true;
	}
	
	@Override
	protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND,
				new ItemStack(LandCraftContent.toolsMap.get(LandiaOreType.GARFAX).getTool(Utils.ToolType.SWORD)));
	}
	
	@Override
	protected ItemStack getSkullDrop() {
        return ItemStack.EMPTY;
    }
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(45.0D);
		this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
	}
	
	@SubscribeEvent
	public static void summonReinforcements(ZombieEvent.SummonAidEvent event) {
		if (event.getSummoner() instanceof EntityZombieCrabman) {
			event.setCustomSummonedAid(new EntityZombieCrabman(event.getWorld()));
		}
	}
	
	@SubscribeEvent
	public static void replaceZombieWithCrabman(LivingSpawnEvent.CheckSpawn event) {
		if (event.getWorld().provider.getDimension() == Config.landiaDimensionID
				&& event.getEntityLiving().getClass() == EntityZombie.class
				&& event.getEntityLiving().getRNG().nextFloat() < 0.27f) {
			event.setResult(Event.Result.DENY);
			event.getWorld().spawnEntity(new EntityZombieCrabman(event.getWorld())); // replace regular zombies
		}
	}
	
	@Override
	@Nullable
    protected ResourceLocation getLootTable() {
		return LOOT;
	}
}
