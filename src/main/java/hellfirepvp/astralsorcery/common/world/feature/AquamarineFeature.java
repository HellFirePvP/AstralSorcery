/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.feature;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
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
 * Class: AquamarineFeature
 * Created by HellFirePvP
 * Date: 25.07.2019 / 22:04
 */
public class AquamarineFeature extends Feature<NoFeatureConfig> {

    public AquamarineFeature() {
        super((dyn) -> NoFeatureConfig.NO_FEATURE_CONFIG);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        return worldIn.setBlockState(pos, BlocksAS.AQUAMARINE_SAND_ORE.getDefaultState(), Constants.BlockFlags.NOTIFY_NEIGHBORS);
    }
}
