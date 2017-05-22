package landmaster.landcraft.proxy;

import landmaster.landcraft.*;
import landmaster.landcraft.entity.*;
import mcjty.lib.tools.*;
import net.minecraft.block.*;
import net.minecraft.block.properties.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.world.storage.loot.LootTableList;

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
		EntityTools.registerModEntity(new ResourceLocation(LandCraft.MODID, "wizard"), EntityWizard.class, "wizard", 0, LandCraft.INSTANCE, 64, 3, true, 0x00FFFF, 0x000000);
		EntityTools.registerModEntity(new ResourceLocation(LandCraft.MODID, "wizard_magic_fireball"), EntityWizardMagicFireball.class, "wizard_magic_fireball", 1, LandCraft.INSTANCE, 64, 1, true);
		
		LootTableList.register(EntityWizard.LOOT);
	}
}
