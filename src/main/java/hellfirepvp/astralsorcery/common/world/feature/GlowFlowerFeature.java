/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.feature;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.util.Constants;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: GlowFlowerFeature
 * Created by HellFirePvP
 * Date: 24.07.2019 / 19:57
 */
public class GlowFlowerFeature extends Feature<NoFeatureConfig> {

    public GlowFlowerFeature() {
        super((dyn) -> new NoFeatureConfig());
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        BlockState glowFlower = BlocksAS.GLOW_FLOWER.getDefaultState();

        if (worldIn.isAirBlock(pos) && glowFlower.isValidPosition(worldIn, pos)) {
            return worldIn.setBlockState(pos, glowFlower, Constants.BlockFlags.DEFAULT);
        }
        return false;
    }
}
