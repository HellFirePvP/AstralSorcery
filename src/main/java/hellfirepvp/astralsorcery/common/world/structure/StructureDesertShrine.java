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

import java.util.Collection;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureDesertShrine
 * Created by HellFirePvP
 * Date: 21.10.2016 / 13:43
 */
public class StructureDesertShrine extends WorldGenAttributeStructure {

    public StructureDesertShrine() {
        super(0, "desertStructure", () -> MultiBlockArrays.desertShrine, BiomeDictionary.Type.SANDY);
    }

    @Override
    public void generate(BlockPos pos, World world, Random rand) {
        getStructureTemplate().placeInWorld(world, pos.down());
    }

    @Override
    public boolean fulfillsSpecificConditions(BlockPos pos, World world, Random random) {
        if(!isDesertBiome(world, pos)) return false;
        if(!canSpawnShrineCorner(world, pos.add(-4, 0,  4))) return false;
        if(!canSpawnShrineCorner(world, pos.add( 4, 0, -4))) return false;
        if(!canSpawnShrineCorner(world, pos.add( 4, 0,  4))) return false;
        if(!canSpawnShrineCorner(world, pos.add(-4, 0, -4))) return false;
        return true;
    }

    @Override
    public BlockPos getGenerationPosition(int chX, int chZ, World world, Random rand) {
        int rX = (chX  * 16) + rand.nextInt(16);
        int rZ = (chZ  * 16) + rand.nextInt(16);
        int rY = world.getTopSolidOrLiquidBlock(new BlockPos(rX, 0, rZ)).getY();
        return new BlockPos(rX, rY, rZ);
    }

    private boolean canSpawnShrineCorner(World world, BlockPos pos) {
        int dY = world.getTopSolidOrLiquidBlock(pos).getY();
        return dY >= cfgEntry.getMinY() && dY <= cfgEntry.getMaxY() && Math.abs(dY - pos.getY()) <= 2 && isDesertBiome(world, pos);
    }

    private boolean isDesertBiome(World world, BlockPos pos) {
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
}
