package landmaster.landcraft.entity.ai;

import java.util.*;
import java.util.function.*;

import landmaster.landcraft.*;
import landmaster.landcraft.capabilities.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraftforge.common.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class EntityAISummonHenchmen<T extends EntityLiving> extends EntityAIBase {
	protected final T owner;
	protected final Function<T, Iterator<? extends EntityLiving>> factory;
	protected final float chance;
	protected final float scatterSize;
	
	static {
		MinecraftForge.EVENT_BUS.register(EntityAISummonHenchmen.class);
	}
	
	@CapabilityInject(IHenchman.class)
	public static Capability<IHenchman> HENCHMAN_CAP = null;
	
	@SubscribeEvent
	public static void attachHenchmen(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject() instanceof EntityLiving) {
			event.addCapability(new ResourceLocation(LandCraft.MODID, "henchman"),
					new ICapabilitySerializable<NBTTagCompound>() {
						private final IHenchman cap = IHenchman.of();

						@Override
						public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
							return capability == HENCHMAN_CAP;
						}

						@SuppressWarnings("unchecked")
						@Override
						public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
							if (capability == HENCHMAN_CAP) {
								return (T)cap;
							}
							return null;
						}

						@Override
						public NBTTagCompound serializeNBT() {
							return IHenchman.STORAGE.writeNBT(HENCHMAN_CAP, cap, null);
						}

						@Override
						public void deserializeNBT(NBTTagCompound nbt) {
							IHenchman.STORAGE.readNBT(HENCHMAN_CAP, cap, null, nbt);
						}
				
			});
		}
	}
	
	@SubscribeEvent
	public static void onSetAttackTarget(LivingSetAttackTargetEvent event) {
		if (event.getEntity().hasCapability(HENCHMAN_CAP, null) && event.getEntity() instanceof EntityLiving) {
			Optional.ofNullable(event.getEntity().getCapability(HENCHMAN_CAP, null).getMaster())
			.map(EntityLiving::getAttackTarget)
			.filter(victim -> !Objects.equals(((EntityLiving)event.getEntity()).getAttackTarget(), victim))
			.ifPresent(victim -> ((EntityLiving)event.getEntity()).setAttackTarget(victim));
		}
	}
	
	public EntityAISummonHenchmen(T owner, Function<T, Iterator<? extends EntityLiving>> factory, float chance, float scatterSize) {
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
	public void startExecuting() {
		
	}
	
	@Override
	public void updateTask() {
		if (owner.getRNG().nextFloat() < chance) {
			factory.apply(owner).forEachRemaining(entity -> {
				entity.setPosition(owner.posX + owner.getRNG().nextFloat() * scatterSize - scatterSize / 2,
						owner.posY, owner.posZ + owner.getRNG().nextFloat() * scatterSize - scatterSize / 2);
				entity.setAttackTarget(owner.getAttackTarget());
				if (entity.hasCapability(HENCHMAN_CAP, null)) {
					entity.getCapability(HENCHMAN_CAP, null).setMaster(owner);
				}
				owner.getEntityWorld().spawnEntity(entity);
			});
		}
	}
}
