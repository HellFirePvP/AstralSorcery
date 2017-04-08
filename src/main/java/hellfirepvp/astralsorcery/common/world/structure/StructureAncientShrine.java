/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.structure;

import hellfirepvp.astralsorcery.common.lib.MultiBlockArrays;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureAncientShrine
 * Created by HellFirePvP
 * Date: 21.10.2016 / 13:38
 */
public class StructureAncientShrine extends WorldGenAttributeStructure {

    public StructureAncientShrine() {
        super(0, "ancientShrine", () -> MultiBlockArrays.ancientShrine, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.SNOWY);
    }

    @Override
    public void generate(BlockPos pos, World world, Random rand) {
        getStructureTemplate().placeInWorld(world, pos);
    }

    @Override
    public boolean fulfillsSpecificConditions(BlockPos pos, World world, Random random) {
        if(!isApplicableBiome(world, pos)) return false;
        if(!canSpawnShrineCorner(world, pos.add(-7, 0,  7))) return false;
        if(!canSpawnShrineCorner(world, pos.add( 7, 0, -7))) return false;
        if(!canSpawnShrineCorner(world, pos.add( 7, 0,  7))) return false;
        if(!canSpawnShrineCorner(world, pos.add(-7, 0, -7))) return false;
        return true;
    }

    @Override
    public BlockPos getGenerationPosition(int chX, int chZ, World world, Random rand) {
        int rX = (chX * 16) + rand.nextInt(16) + 8;
        int rZ = (chZ * 16) + rand.nextInt(16) + 8;
        int rY = world.getTopSolidOrLiquidBlock(new BlockPos(rX, 0, rZ)).getY();
        return new BlockPos(rX, rY, rZ);
    }

    private boolean canSpawnShrineCorner(World world, BlockPos pos) {
        int dY = world.getTopSolidOrLiquidBlock(pos).getY();
        return dY >= cfgEntry.getMinY() && dY <= cfgEntry.getMaxY() && Math.abs(dY - pos.getY()) <= 6 && isApplicableBiome(world, pos);
    }

    private boolean isApplicableBiome(World world, BlockPos pos) {
        if(cfgEntry.shouldIgnoreBiomeSpecifications()) return true;

        Biome b = world.getBiome(pos);
        BiomeDictionary.Type[] types = BiomeDictionary.getTypesForBiome(b);
        if(types == null || types.length == 0) return false;
        boolean applicable = false;
        for (BiomeDictionary.Type t : types) {
            if (cfgEntry.getTypes().contains(t)) applicable = true;
        }
        return applicable;
    }
}
