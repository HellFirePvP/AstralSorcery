/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.world.feature;

import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.structure.StructureBlockArray;
import hellfirepvp.astralsorcery.common.structure.types.StructureType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StructureGenerationFeature
 * Created by HellFirePvP
 * Date: 23.07.2019 / 07:56
 */
public class StructureGenerationFeature extends Feature<NoFeatureConfig> {

    private final StructureBlockArray structure;
    private final StructureType type;

    public StructureGenerationFeature(StructureType type, StructureBlockArray structure) {
        super((dynamic) -> NoFeatureConfig.NO_FEATURE_CONFIG, true);
        this.type = type;
        this.structure = structure;
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        this.structure.placeInWorld(worldIn, pos, at -> {
            int chX = at.getX() >> 4;
            int chZ = at.getZ() >> 4;
            return worldIn.chunkExists(chX, chZ) || worldIn.getWorld().chunkExists(chX, chZ);
        });
        DataAS.DOMAIN_AS.getData(worldIn.getWorld(), DataAS.KEY_STRUCTURE_GENERATION).setStructureGeneration(this.type, pos);
        return true;
    }
}
