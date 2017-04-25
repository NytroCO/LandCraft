package landmaster.landcraft.world;

import java.util.*;

import com.google.common.base.Objects;

import mcjty.lib.compat.*;
import mcjty.lib.tools.*;
import net.minecraft.entity.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.*;
import net.minecraftforge.event.terraingen.*;

public class LandiaChunkGenerator implements CompatChunkGenerator {
	private final World worldObj;
	private Random random;
    private Biome[] biomesForGeneration;
    
    @SuppressWarnings("unchecked")
	private static final List<Biome.SpawnListEntry> mobs = new ArrayList<>(Arrays.asList(
    		new Biome.SpawnListEntry((Class<? extends EntityLiving>)
    				Objects.firstNonNull(EntityTools.findClassById("landcore:landlord"),
    						EntityTools.findClassById("landlord")), 10, 1, 2)));
    
    private MapGenBase caveGenerator = new MapGenCaves();
    private NormalTerrainGenerator terraingen = new NormalTerrainGenerator();
	
	public LandiaChunkGenerator(World world) {
		this.worldObj = world;
		long seed = world.getSeed();
		random = new Random((seed+516)*314);
		terraingen.setup(worldObj, random);
        caveGenerator = TerrainGen.getModdedMapGen(caveGenerator, InitMapGenEvent.EventType.CAVE);
	}
	
	@Override
	public Chunk provideChunk(int x, int z) {
		ChunkPrimer chunkprimer = new ChunkPrimer();

        // Setup biomes for terraingen
        this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomesForGeneration(this.biomesForGeneration, x * 4 - 2, z * 4 - 2, 10, 10);
        terraingen.setBiomesForGeneration(biomesForGeneration);
        terraingen.generate(x, z, chunkprimer);

        // Setup biomes again for actual biome decoration
        this.biomesForGeneration = this.worldObj.getBiomeProvider().getBiomes(this.biomesForGeneration, x * 16, z * 16, 16, 16);
        // This will replace stone with the biome specific stones
        terraingen.replaceBiomeBlocks(x, z, chunkprimer, this, biomesForGeneration);

        // Generate caves
        this.caveGenerator.generate(this.worldObj, x, z, chunkprimer);

        Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);

        byte[] biomeArray = chunk.getBiomeArray();
        for (int i = 0; i < biomeArray.length; ++i) {
            biomeArray[i] = (byte)Biome.getIdForBiome(this.biomesForGeneration[i]);
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

        // Add biome decorations (like flowers, grass, trees, ...)
        biome.decorate(this.worldObj, this.random, blockpos);

        // Make sure animals appropriate to the biome spawn here when the chunk is generated
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
		if (creatureType == EnumCreatureType.MONSTER) entries.addAll(mobs);
		return entries;
	}

	@Override
	public void recreateStructures(Chunk chunkIn, int x, int z) {
	}

	@Override
	public BlockPos clGetStrongholdGen(World worldIn, String structureName, BlockPos position) {
		return null;
	}
}
