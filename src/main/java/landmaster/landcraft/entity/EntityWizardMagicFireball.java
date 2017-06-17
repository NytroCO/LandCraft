package landmaster.landcraft.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.projectile.*;
import net.minecraft.init.*;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;

public class EntityWizardMagicFireball extends EntityFireball {
	public EntityWizardMagicFireball(World worldIn)
    {
        super(worldIn);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityWizardMagicFireball(World worldIn, EntityLivingBase shooter, double accelX, double accelY, double accelZ)
    {
        super(worldIn, shooter, accelX, accelY, accelZ);
        this.setSize(0.3125F, 0.3125F);
    }

    public EntityWizardMagicFireball(World worldIn, double x, double y, double z, double accelX, double accelY, double accelZ)
    {
        super(worldIn, x, y, z, accelX, accelY, accelZ);
        this.setSize(0.3125F, 0.3125F);
    }

    /**
     * Called when this EntityFireball hits a block or entity.
     */
    protected void onImpact(RayTraceResult result)
    {
        if (!this.world.isRemote)
        {
            if (result.entityHit != null)
            {
            	boolean flag = result.entityHit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, this.shootingEntity), 5.0F);
            	if (flag)
                {
                    this.applyEnchantments(this.shootingEntity, result.entityHit);
                    if (result.entityHit instanceof EntityLivingBase) {
                    	((EntityLivingBase)result.entityHit).addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 130, 1));
                    	((EntityLivingBase)result.entityHit).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 130, 1));
                    }
                }
            }
            
            this.world.createExplosion(this, result.hitVec.x, result.hitVec.y, result.hitVec.z, 0.5f, false);

            this.setDead();
        }
    }

    /**
     * Returns true if other Entities should be prevented from moving through this Entity.
     */
    public boolean canBeCollidedWith()
    {
        return false;
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        return false;
    }
}
