package landmaster.landcraft.capabilities;

import java.util.*;

import javax.annotation.*;

import net.minecraft.entity.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.fml.common.*;

public interface IHenchman {
	public @Nullable EntityLiving getMaster();
	public void setMaster(@Nullable EntityLiving master);
	
	public static void initCaps() {
		CapabilityManager.INSTANCE.register(IHenchman.class, STORAGE, Default::new);
	}
	
	public static IHenchman of() {
		return new Default();
	}
	
	public static IHenchman of(@Nullable EntityLiving master) {
		return new Default(master);
	}
	
	public static class Default implements IHenchman {
		private EntityLiving master;
		
		public Default() {
			this(null);
		}
		
		public Default(@Nullable EntityLiving master) {
			this.master = master;
		}

		@Override
		public @Nullable EntityLiving getMaster() {
			return master;
		}

		@Override
		public void setMaster(@Nullable EntityLiving master) {
			this.master = master;
		}
	}
	
	public static Storage STORAGE = new Storage();
	
	public static class Storage implements Capability.IStorage<IHenchman> {
		private Storage() {}

		@Override
		public NBTTagCompound writeNBT(Capability<IHenchman> capability, IHenchman instance, EnumFacing side) {
			NBTTagCompound nbt = new NBTTagCompound();
			if (instance.getMaster() != null) {
				UUID uuid = instance.getMaster().getUniqueID();
				nbt.setUniqueId("UUID", uuid);
			}
			return nbt;
		}

		@Override
		public void readNBT(Capability<IHenchman> capability, IHenchman instance, EnumFacing side, NBTBase nbt) {
			if (nbt instanceof NBTTagCompound && ((NBTTagCompound)nbt).hasUniqueId("UUID")) {
				Entity ent = FMLCommonHandler.instance().getMinecraftServerInstance()
						.getEntityFromUuid(((NBTTagCompound)nbt).getUniqueId("UUID"));
				if (ent instanceof EntityLiving) {
					instance.setMaster((EntityLiving)ent);
				}
			}
		}
		
	}
}
