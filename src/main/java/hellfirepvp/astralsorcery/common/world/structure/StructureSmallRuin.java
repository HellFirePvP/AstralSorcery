/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.world.data.StructureGenBuffer;
import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import hellfirepvp.astralsorcery.common.world.task.StructureRuinTask;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureSmallRuin
 * Created by HellFirePvP
 * Date: 25.01.2018 / 20:06
 */
public class StructureSmallRuin extends WorldGenAttributeStructure {

    private int heightThreshold = 0;

    public StructureSmallRuin() {
        super(3, "smallRuin", () -> MultiBlockArrays.smallRuin, StructureGenBuffer.StructureType.SMALL_RUIN, true);
        this.idealDistance = 2048F;
    }

    @Override
    public void generate(BlockPos pos, World world, Random rand) {
        getStructureTemplate().placeInWorld(world, pos);
        getBuffer(world).markStructureGeneration(pos, getStructureType());

        //Should be the case...
        //if (world instanceof WorldServer) {
        //    AstralSorcery.proxy.scheduleDelayed(new StructureRuinTask((WorldServer) world, pos, rand));
        //}
    }

    @Override
    public boolean fulfillsSpecificConditions(BlockPos pos, World world, Random random) {
        if(!isApplicableWorld(world)) return false;
        if(!isApplicableBiome(world, pos)) return false;
        if(!canSpawnPosition(world, pos.add(-1, 0,  5))) return false;
        if(!canSpawnPosition(world, pos.add( 1, 0, -5))) return false;
        if(!canSpawnPosition(world, pos.add( 1, 0,  5))) return false;
        if(!canSpawnPosition(world, pos.add(-1, 0, -5))) return false;
        return true;
    }

    private boolean isApplicableWorld(World world) {
        if(cfgEntry.shouldIgnoreDimensionSpecifications()) return true;

        Integer dimId = world.provider.getDimension();
        if(cfgEntry.getApplicableDimensions().isEmpty()) return false;
        for (Integer dim : cfgEntry.getApplicableDimensions()) {
            if(dim.equals(dimId)) return true;
        }
        return false;
    }

    private boolean isApplicableBiome(World world, BlockPos pos) {
        if(cfgEntry.shouldIgnoreBiomeSpecifications()) return true;

        Biome b = world.getBiome(pos);
        Collection<BiomeDictionary.Type> types = BiomeDictionary.getTypes(b);
        if(types.isEmpty()) return false;
        boolean applicable = false;
        for (BiomeDictionary.Type t : types) {
            if (cfgEntry.getTypes().contains(t)) applicable = true;
        }
        return applicable;
    }

    private boolean canSpawnPosition(World world, BlockPos pos) {
        int dY = world.getTopSolidOrLiquidBlock(pos).getY();
        if (dY >= cfgEntry.getMinY() && dY <= cfgEntry.getMaxY() && Math.abs(dY - pos.getY()) <= heightThreshold) {
            IBlockState at = world.getBlockState(new BlockPos(pos.getX(), dY, pos.getZ()));
            return !at.getMaterial().isLiquid() && isApplicableBiome(world, pos);
        }
        return false;
    }

    @Nullable
    @Override
    public BlockPos getGenerationPosition(int chX, int chZ, World world, Random rand) {
        int rX = (chX * 16) + rand.nextInt(16) + 8;
        int rZ = (chZ * 16) + rand.nextInt(16) + 8;
        int rY = world.getTopSolidOrLiquidBlock(new BlockPos(rX, 0, rZ)).getY();
        return new BlockPos(rX, rY - 1, rZ);
    }

    @Override
    protected void loadAdditionalConfigEntries(Configuration cfg) {
        super.loadAdditionalConfigEntries(cfg);

        heightThreshold = cfg.getInt("heightThreshold", cfgEntry.getConfigurationSection(), heightThreshold, 1, 32,
                "Defines how high/low the surface in comparison to the structure can be to be seen as 'sufficiently flat' for the structure to spawn at the given position.");
    }
}
