package landmaster.landcraft.proxy;

import java.util.*;
import java.util.function.*;

import landmaster.landcraft.*;
import landmaster.landcraft.api.ModInfo;
import landmaster.landcraft.entity.*;
import net.minecraft.block.*;
import net.minecraft.block.properties.*;
import net.minecraft.block.state.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.storage.loot.*;
import net.minecraftforge.fml.common.registry.*;

public class CommonProxy {
	public void registerItemRenderer(Item item, int meta, String id) {
	}
	
	public void registerItemRenderer(Item item, int meta, String id, String variant) {
	}
	
	public void bindTESRs() {
	}
	
	public void setCustomStateMapper(Block block, IProperty<?>... ignore) {
	}
	
	public void initColorHandlers() {
	}
	
	public void initEntities() {
		EntityRegistry.registerModEntity(new ResourceLocation(ModInfo.MODID, "wizard"), EntityWizard.class, "wizard", 0, LandCraft.INSTANCE, 64, 3, true, 0x00FFFF, 0x000000);
		EntityRegistry.registerModEntity(new ResourceLocation(ModInfo.MODID, "wizard_magic_fireball"), EntityWizardMagicFireball.class, "wizard_magic_fireball", 1, LandCraft.INSTANCE, 64, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation(ModInfo.MODID, "big_brother"), EntityBigBrother.class, "big_brother", 2, LandCraft.INSTANCE, 128, 2, true);
		EntityRegistry.registerModEntity(new ResourceLocation(ModInfo.MODID, "zombie_crabman"), EntityZombieCrabman.class, "zombie_crabman", 3, LandCraft.INSTANCE, 64, 3, true, 0x4286F4, 0xFF9000);
		
		LootTableList.register(EntityWizard.LOOT);
		LootTableList.register(EntityBigBrother.LOOT);
		LootTableList.register(EntityZombieCrabman.LOOT);
	}
	
	public void setCustomStateMapper(Block block, Function<Block, Collection<Map.Entry<IBlockState, String>>> mapper) {
	}
}
