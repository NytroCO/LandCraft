package landmaster.landcraft.world;

import java.util.*;

import javax.annotation.*;

import com.google.common.collect.*;

import landmaster.landcore.entity.*;
import landmaster.landcraft.entity.*;
import net.minecraft.entity.*;
import net.minecraft.init.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.structure.*;
import net.minecraftforge.event.terraingen.*;

public class LandiaChunkGenerator implements IChunkGenerator {
	private final World worldObj;
	private final Random random;
	private Biome[] biomesForGeneration;
	
	private static final List<Biome.SpawnListEntry> mobs = ImmutableList.of(
			new Biome.SpawnListEntry(EntityLandlord.class, 40, 1, 3),
			new Biome.SpawnListEntry(EntityWizard.class, 27, 1, 3),
			new Biome.SpawnListEntry(EntityZombieCrabman.class, 46, 2, 5));
	
	private MapGenBase caveGenerator = new MapGenCaves();
	private MapGenBase ravineGenerator = new MapGenRavine();
	private MapGenMineshaft mineshaftGenerator = new MapGenMineshaft();
	private final NormalTerrainGenerator terraingen = new NormalTerrainGenerator();
	
	public LandiaChunkGenerator(World world) {
		this.worldObj = world;
		long seed = world.getSeed();
		random = new Random((seed + 516) * 314);
		terraingen.setup(worldObj, random);
		caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, InitMapGenEvent.EventType.CAVE);
		mineshaftGenerator = (MapGenMineshaft) TerrainGen.getModdedMapGen(mineshaftGenerator,
				InitMapGenEvent.EventType.MINESHAFT);
		ravineGenerator = TerrainGen.getModdedMapGen(ravineGenerator, InitMapGenEvent.EventType.RAVINE);
	}
	
	@Override
	public Chunk generateChunk(int x, int z) {
		ChunkPrimer chunkprimer = new ChunkPrimer();
		
		// Setup biomes for terraingen
		this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration,
				x * 4 - 2, z * 4 - 2, 10, 10);
		terraingen.setBiomesForGeneration(biomesForGeneration);
		terraingen.generate(x, z, chunkprimer);
		
		// Setup biomes again for actual biome decoration
		this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomes(this.biomesForGeneration, x * 16, z * 16,
				16, 16);
		// This will replace stone with the biome specific stones
		terraingen.replaceBiomeBlocks(x, z, chunkprimer, this, biomesForGeneration);
		
		// Generate caves
		this.caveGenerator.generate(this.worldObj, x, z, chunkprimer);
		
		// Generate ravines
		this.ravineGenerator.generate(this.worldObj, x, z, chunkprimer);
		
		// Generate mineshafts
		this.mineshaftGenerator.generate(this.worldObj, x, z, chunkprimer);
		
		Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
		
		byte[] biomeArray = chunk.getBiomeArray();
		for (int i = 0; i < biomeArray.length; ++i) {
			biomeArray[i] = (byte) Biome.getIdForBiome(this.biomesForGeneration[i]);
		}
		
		chunk.generateSkylightMap();
		return chunk;
	}
	
	@Override
	public void populate(int x, int z) {
		int i = x * 16;
		int j = z * 16;
		BlockPos blockpos = new BlockPos(i, 0, j);
		Biome biome = this.worldObj.getBiome(blockpos.add(16, 0, 16));
		
		ChunkPos chunkpos = new ChunkPos(x, z);
		
		this.mineshaftGenerator.generateStructure(this.worldObj, this.random, chunkpos);
		
		if (TerrainGen.populate(this, this.worldObj, this.random, x, z, false, net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAKE) && random.nextFloat() < 0.3f) {
            int i1 = this.random.nextInt(16) + 8;
            int j1 = this.random.nextInt(256);
            int k1 = this.random.nextInt(16) + 8;
            (new WorldGenLakes(Blocks.WATER)).generate(this.worldObj, this.random, blockpos.add(i1, j1, k1));
        }
		
		// Add biome decorations (like flowers, grass, trees, ...)
		biome.decorate(this.worldObj, this.random, blockpos);
		
		// Make sure animals appropriate to the biome spawn here when the chunk
		// is generated
		WorldEntitySpawner.performWorldGenSpawning(this.worldObj, biome, i + 8, j + 8, 16, 16, this.random);
	}
	
	@Override
	public boolean generateStructures(Chunk chunkIn, int x, int z) {
		return false;
	}
	
	@Override
	public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
		Biome biome = this.worldObj.getBiome(pos);
		List<Biome.SpawnListEntry> entries = new ArrayList<>(biome.getSpawnableList(creatureType));
		if (creatureType == EnumCreatureType.MONSTER) {
			entries.addAll(mobs);
		}
		return entries;
	}
	
	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z) {
		this.mineshaftGenerator.generate(this.worldObj, x, z, null);
	}
	
	@Override
	@Nullable
    public BlockPos getNearestStructurePos(World worldIn, String structureName, BlockPos position, boolean findUnexplored) {
		return null;
	}

	@Override
	public boolean isInsideStructure(World worldIn, String structureName, BlockPos pos) {
		return false;
	}
}
