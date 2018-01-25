/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world;

import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.config.entry.WorldGenEntry;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.StructureGenBuffer;
import hellfirepvp.astralsorcery.common.util.struct.StructureBlockArray;
import hellfirepvp.astralsorcery.common.world.structure.WorldGenAttributeStructure;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldGenAttributeCommon
 * Created by HellFirePvP
 * Date: 21.10.2016 / 13:24
 */
public abstract class WorldGenAttributeCommon extends WorldGenAttribute {

    protected final WorldGenEntry cfgEntry;
    private final boolean onlyGenerateInSkyDimensions;

    public WorldGenAttributeCommon(int attributeVersion, boolean onlyGenerateInSkyDimensions, String entry, boolean ignoreBiomeSpecifications, BiomeDictionary.Type... types) {
        this(attributeVersion, 140, onlyGenerateInSkyDimensions, entry, ignoreBiomeSpecifications, types);
    }

    public WorldGenAttributeCommon(int attributeVersion, int defaultChance, boolean onlyGenerateInSkyDimensions, String entry, boolean ignoreBiomeSpecifications, BiomeDictionary.Type... types) {
        super(attributeVersion);
        this.onlyGenerateInSkyDimensions = onlyGenerateInSkyDimensions;
        this.cfgEntry = new WorldGenEntry(entry, defaultChance, ignoreBiomeSpecifications, types) {
            @Override
            public void loadFromConfig(Configuration cfg) {
                super.loadFromConfig(cfg);
                loadAdditionalConfigEntries(cfg);
            }
        };
        Config.addDynamicEntry(cfgEntry);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world) {
        if(canGenerateAtAll(chunkX, chunkZ, world, random)) {
            BlockPos pos = getGenerationPosition(chunkX, chunkZ, world, random);
            if(pos != null) {
                tryGenerateAtPosition(pos, world, random);
            }
        }
    }

    public void tryGenerateAtPosition(BlockPos pos, World world, Random random) {
        if(fulfillsSpecificConditions(pos, world, random)) {
            generate(pos, world, random);
        }
    }

    protected void loadAdditionalConfigEntries(Configuration cfg) {}

    public abstract void generate(BlockPos pos, World world, Random rand);

    public abstract boolean fulfillsSpecificConditions(BlockPos pos, World world, Random random);

    @Nullable
    public abstract BlockPos getGenerationPosition(int chX, int chZ, World world, Random rand);

    public boolean canGenerateAtAll(int chX, int chZ, World world, Random rand) {
        if(!cfgEntry.shouldGenerate()) return false;
        if(onlyGenerateInSkyDimensions && !Config.worldGenDimWhitelist.contains(world.provider.getDimension())) return false;
        double chanceMultiplier = 1F;
        if(Config.respectIdealDistances && this instanceof WorldGenAttributeStructure) {
            StructureGenBuffer.StructureType type = ((WorldGenAttributeStructure) this).getStructureType();
            StructureGenBuffer buf = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_GEN);
            BlockPos pos = new BlockPos(chX * 16, 0, chZ * 16);
            double dst = buf.getDstToClosest(type, new BlockPos(pos.getX(), world.getTopSolidOrLiquidBlock(pos).getY(), pos.getZ()));
            if(dst != -1) {
                if(dst < 32) {
                    return false;
                }
                double ideal = ((WorldGenAttributeStructure) this).getIdealDistance();
                chanceMultiplier = ideal / dst;
            } else {
                chanceMultiplier = 0F;
            }
        }
        return cfgEntry.tryGenerate(rand, chanceMultiplier);
    }

    public static interface StructureQuery {

        public StructureBlockArray getStructure();

    }

}
