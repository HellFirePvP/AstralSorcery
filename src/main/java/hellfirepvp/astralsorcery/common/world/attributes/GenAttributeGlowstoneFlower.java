/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.attributes;

import hellfirepvp.astralsorcery.common.block.BlockCustomFlower;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.world.WorldGenAttributeCommon;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;

import java.util.Collection;
import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GenAttributeGlowstoneFlower
 * Created by HellFirePvP
 * Date: 30.03.2017 / 09:04
 */
public class GenAttributeGlowstoneFlower extends WorldGenAttributeCommon {

    private static boolean isGeneratingAdditional = false;
    private boolean ignoreSnowCondition = false;

    public GenAttributeGlowstoneFlower() {
        super(1, 2, false, "glowstone_flower", false, BiomeDictionary.Type.MOUNTAIN, BiomeDictionary.Type.COLD);
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

    @Override
    protected void loadAdditionalConfigEntries(Configuration cfg) {
        ignoreSnowCondition = cfg.getBoolean("ignoreSnowCondition", cfgEntry.getConfigurationSection(), false, "Set this to true and the decorator will ignore the spawn-condition if snow is/can fall in the area.");
    }

    @Override
    public void generate(BlockPos pos, World world, Random rand) {
        world.setBlockState(pos, BlocksAS.customFlower.getDefaultState().withProperty(BlockCustomFlower.FLOWER_TYPE, BlockCustomFlower.FlowerType.GLOW_FLOWER));
        if(!isGeneratingAdditional) {
            isGeneratingAdditional = true;
            try {
                for (int i = 0; i < 8; i++) {
                    if(rand.nextInt(4) == 0) {
                        tryGenerateAtPosition(randomOffset(world, pos, rand, 7), world, rand);
                    }
                }
            } finally {
                isGeneratingAdditional = false;
            }
        }
    }

    private BlockPos randomOffset(World world, BlockPos origin, Random random, int offsetRand) {
        int rX = origin.getX() - offsetRand + random.nextInt(offsetRand * 2 + 1);
        int rZ = origin.getZ() - offsetRand + random.nextInt(offsetRand * 2 + 1);
        int rY = world.getPrecipitationHeight(new BlockPos(rX, 0, rZ)).getY();
        return new BlockPos(rX, rY, rZ);
    }

    @Override
    public boolean fulfillsSpecificConditions(BlockPos pos, World world, Random random) {
        return isApplicableBiome(world, pos) &&
                pos.getY() >= cfgEntry.getMinY() && pos.getY() <= cfgEntry.getMaxY() &&
                world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP) &&
                (ignoreSnowCondition || world.canSnowAt(pos, false));
    }

    @Override
    public BlockPos getGenerationPosition(int chX, int chZ, World world, Random rand) {
        int rX = (chX  * 16) + rand.nextInt(16) + 8;
        int rZ = (chZ  * 16) + rand.nextInt(16) + 8;
        int rY = world.getPrecipitationHeight(new BlockPos(rX, 0, rZ)).getY();
        return new BlockPos(rX, rY, rZ);
    }

}
