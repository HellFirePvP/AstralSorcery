/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.StructureGenBuffer;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.structure.array.BlockArray;
import hellfirepvp.astralsorcery.common.structure.array.StructureBlockArray;
import hellfirepvp.astralsorcery.common.world.WorldGenAttributeCommon;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldGenAttributeStructure
 * Created by HellFirePvP
 * Date: 30.03.2017 / 10:51
 */
public abstract class WorldGenAttributeStructure extends WorldGenAttributeCommon {

    private static boolean generatingStructure = false;

    protected final StructureGenBuffer.StructureType type;
    protected float idealDistance = 256F;
    private final StructureQuery query;

    public WorldGenAttributeStructure(int attributeVersion, String entry, StructureQuery query, StructureGenBuffer.StructureType type, boolean ignoreBiomeSpecifications, BiomeDictionary.Type... types) {
        super(attributeVersion, entry, ignoreBiomeSpecifications, types);
        this.query = query;
        this.type = type;
    }

    public WorldGenAttributeStructure(int attributeVersion, int defaultChance, String entry, StructureQuery query, StructureGenBuffer.StructureType type, boolean ignoreBiomeSpecifications, BiomeDictionary.Type... types) {
        super(attributeVersion, defaultChance, entry, ignoreBiomeSpecifications, types);
        this.query = query;
        this.type = type;
    }

    public StructureGenBuffer.StructureType getStructureType() {
        return type;
    }

    public float getIdealDistance() {
        return idealDistance;
    }

    public StructureBlockArray getStructureTemplate() {
        return query.getStructure();
    }

    protected StructureGenBuffer getBuffer(World world) {
        return WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STRUCTURE_GEN);
    }

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world) {
        if(generatingStructure) return;
        generatingStructure = true;
        try {
            super.generate(random, chunkX, chunkZ, world);
        } finally {
            generatingStructure = false;
        }
    }

    protected void generateAsSubmergedStructure(World world, BlockPos center) {
        Map<BlockPos, IBlockState> prevStates = Maps.newHashMap();
        BlockArray array = this.query.getStructure();
        for (BlockPos offset : array.getPattern().keySet()) {
            BlockPos at = offset.add(center);
            prevStates.put(at, world.getBlockState(at));
        }
        Map<BlockPos, IBlockState> placedStates = array.placeInWorld(world, center);
        List<BlockPos> positions = Lists.newLinkedList(prevStates.keySet());
        MiscUtils.mergeList(placedStates.keySet(), positions);
        IBlockState airState = Blocks.AIR.getDefaultState();
        for (BlockPos pos : positions) {
            IBlockState prev = prevStates.getOrDefault(pos, airState);
            IBlockState now  = placedStates.getOrDefault(pos, airState);
            if (MiscUtils.isFluidBlock(prev)) {
                BlockPos it = pos;
                while (now.getBlock().isAir(now, world, it) && world.setBlockState(it, prev)) {
                    it = it.down();
                    if (!placedStates.containsKey(it)) {
                        break;
                    }
                    now = placedStates.getOrDefault(it, airState);
                }
            }
        }
    }

    @Override
    protected void loadAdditionalConfigEntries(Configuration cfg) {
        super.loadAdditionalConfigEntries(cfg);

        idealDistance = cfg.getFloat("idealDistance", cfgEntry.getConfigurationSection(), idealDistance, 1F, 16384F,
                "Sets the 'ideal' distance between 2 structures of the same type. If the distance is lower, it's unlikely that the same type of structure will spawn," +
                        "if it's higher it's more likely that this type of structure will spawn. Only has influence if the config option 'respectIdealStructureDistances' is enabled.");
    }
}
