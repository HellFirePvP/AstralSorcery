/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.feature;

import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.DataAS;
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
 * Class: RockCrystalFeature
 * Created by HellFirePvP
 * Date: 24.07.2019 / 22:40
 */
public class RockCrystalFeature extends Feature<NoFeatureConfig> {

    public RockCrystalFeature() {
        super((dyn) -> NoFeatureConfig.NO_FEATURE_CONFIG);
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        DataAS.DOMAIN_AS.getData(worldIn, DataAS.KEY_ROCK_CRYSTAL_BUFFER).addOre(pos);
        return worldIn.setBlockState(pos, BlocksAS.ROCK_CRYSTAL_ORE.getDefaultState(), Constants.BlockFlags.DEFAULT);
    }
}
