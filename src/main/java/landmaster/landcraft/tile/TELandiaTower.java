package landmaster.landcraft.tile;

import java.util.*;
import java.util.Optional;
import java.util.function.*;

import javax.annotation.*;

import gnu.trove.set.hash.*;
import landmaster.landcore.api.*;
import landmaster.landcraft.block.*;
import landmaster.landcraft.config.*;
import landmaster.landcraft.content.*;
import landmaster.landcraft.entity.*;
import landmaster.landcraft.net.*;
import landmaster.landcraft.util.*;
import net.minecraft.block.state.*;
import net.minecraft.init.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraftforge.common.*;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraftforge.fml.relauncher.*;

public class TELandiaTower extends TileEntity implements ITickable {
	public static final int SUB_DIST = 4;
	public static final int SUB_HEIGHT = 2;
	
	private @Nullable UUID targetEntity;
	
	static {
		MinecraftForge.EVENT_BUS.register(TELandiaTower.class);
		
		PacketHandler.registerTEHandler(TELandiaTower.class,
				new Handle<>(UpdateTELandiaTower::new, UpdateTELandiaTower::onMessage));
	}
	
	@Override
	public void onLoad() {
		if (world.isRemote) {
			PacketHandler.INSTANCE.sendToServer(new PacketRequestUpdateTELandiaTower(new Coord4D(this)));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(pos, new BlockPos(pos.getX()+1, world.getHeight(), pos.getZ()+1));
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("TargetEntity")) {
			targetEntity = compound.getUniqueId("TargetEntity");
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound = super.writeToNBT(compound);
		if (targetEntity != null) {
			compound.setUniqueId("TargetEntity", targetEntity);
		}
		return compound;
	}
	
	public UUID getTargetEntity() {
		return targetEntity;
	}
	
	private static final Map<EnumFacing, Predicate<IBlockState>> DIRECTION_TO_METAL = new EnumMap<>(EnumFacing.class);
	
	private static Predicate<IBlockState> matchMetal(LandiaOreType type) {
		return state -> state.getBlock() == LandCraftContent.landia_metal && state.getValue(BlockLandiaMetal.TYPE) == type;
	}
	
	static {
		DIRECTION_TO_METAL.put(EnumFacing.NORTH, matchMetal(LandiaOreType.FRISCION));
		DIRECTION_TO_METAL.put(EnumFacing.EAST, matchMetal(LandiaOreType.MORGANINE));
		DIRECTION_TO_METAL.put(EnumFacing.SOUTH, matchMetal(LandiaOreType.RACHELINE));
		DIRECTION_TO_METAL.put(EnumFacing.WEST, matchMetal(LandiaOreType.GARFAX));
		
		// special case(s)
		DIRECTION_TO_METAL.put(EnumFacing.UP, matchMetal(LandiaOreType.KELLINE));
		DIRECTION_TO_METAL.put(EnumFacing.DOWN,
				state -> state.getBlock().getRegistryName().equals(new ResourceLocation("landcore:block_metal"))
				&& state.getBlock().getMetaFromState(state) == 2); // Landium
	}
	
	public boolean checkTowers() {
		return checkTowersSpec().isPresent();
	}
	
	public Optional<Set<BlockPos>> checkTowersSpec() {
		final Set<BlockPos> removePositions = new THashSet<>();
		
		for (EnumFacing facing: EnumFacing.HORIZONTALS) {
			for (int j=0; j<SUB_HEIGHT; ++j) {
				BlockPos bpos = pos.offset(facing, SUB_DIST).add(0, j, 0);
				IBlockState state = getWorld().getBlockState(bpos);
				if (!DIRECTION_TO_METAL.get(facing).test(state)) {
					return Optional.empty();
				}
				if (j == SUB_HEIGHT-1) {
					removePositions.add(bpos); // destroy top block
				}
			}
			
			if (facing.getAxis() == EnumFacing.Axis.Z) {
				removePositions.add(pos.offset(facing).add(0, 1, 0)); // destroy Kelline
			} else if (facing.getAxis() == EnumFacing.Axis.X) {
				removePositions.add(pos.offset(facing)); // destroy Landium
			}
			
			final IBlockState Lstate = getWorld().getBlockState(pos.offset(facing));
			final IBlockState Kstate = getWorld().getBlockState(pos.offset(facing).add(0, 1, 0));
			
			if (!(DIRECTION_TO_METAL.get(EnumFacing.DOWN)
					.test(Lstate)
					&& DIRECTION_TO_METAL.get(EnumFacing.UP)
					.test(Kstate))) {
				return Optional.empty();
			}
		}
		return Optional.of(removePositions);
	}
	
	@SubscribeEvent
	public static void detectEntity(LivingDeathEvent event) {
		if (!event.getEntity().getEntityWorld().isRemote
				&& event.getEntity().getEntityWorld().provider.getDimension() == Config.landiaDimensionID
				&& event.getEntity() instanceof EntityWizard) {
			List<TELandiaTower> tiles = Utils.getTileEntitiesWithinAABB(
					event.getEntity().getEntityWorld(), TELandiaTower.class,
					Utils.AABBfromVecs(event.getEntity().getPositionVector().subtract(5, 5, 5),
							event.getEntity().getPositionVector().addVector(5, 5, 5)));
			tiles.stream()
			.filter(tile -> tile.targetEntity == null)
			.forEach(tile -> {
				tile.checkTowersSpec()
				.ifPresent(set -> {
					set.forEach(bpos -> tile.world.setBlockState(bpos, Blocks.AIR.getDefaultState(), 0x2));
					EntityBigBrother orwellEnemy = new EntityBigBrother(tile.getWorld()); // SUMMON THE BOSS!!
					tile.targetEntity = orwellEnemy.getUniqueID();
					tile.syncTE();
					orwellEnemy.setLocationAndAngles(tile.pos.getX(), tile.pos.getY()+BlockLandiaTower.MAX_POSITION+1, tile.pos.getZ(),
							orwellEnemy.getRNG().nextFloat()*360, 0);
					tile.world.spawnEntity(orwellEnemy);
				});
			});
		}
	}
	
	public void setTargetEntity(UUID uuid) {
		this.targetEntity = uuid;
	}
	
	private void syncTE() {
		PacketHandler.INSTANCE.sendToDimension(new PacketUpdateTE(new Coord4D(this), new UpdateTELandiaTower(targetEntity)), this.world.provider.getDimension());
	}
	
	private boolean _delay = true;
	
	@Override
	public void update() {
		if (!world.isRemote && targetEntity != null
				&& FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(targetEntity) == null) {
			if (_delay) {
				_delay = false;
			} else {
				targetEntity = null;
				this.syncTE();
				_delay = true;
			}
		}
	}
}
