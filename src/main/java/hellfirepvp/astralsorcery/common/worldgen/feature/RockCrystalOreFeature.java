/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.feature;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.lib.BlocksAS;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.BulkSectionAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RockCrystalOreFeature
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RockCrystalOreFeature extends Feature<RockCrystalOreFeatureConfiguration> {

    public RockCrystalOreFeature(Codec<RockCrystalOreFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RockCrystalOreFeatureConfiguration> context) {
        BlockPos blockpos = context.origin();
        WorldGenLevel worldgenlevel = context.level();
        RockCrystalOreFeatureConfiguration cfg = context.config();

        if (worldgenlevel.ensureCanWrite(blockpos)) {
            try (BulkSectionAccess access = new BulkSectionAccess(worldgenlevel)) {
                LevelChunkSection section = access.getSection(blockpos);
                if (section != null) {
                    int relativeX = SectionPos.sectionRelative(blockpos.getX());
                    int relativeY = SectionPos.sectionRelative(blockpos.getY());
                    int relativeZ = SectionPos.sectionRelative(blockpos.getZ());
                    if (cfg.getReplaceCondition().test(worldgenlevel, blockpos) && !isAdjacentToAir(access::getBlockState, blockpos)) {
                        section.setBlockState(relativeX, relativeY, relativeZ, BlocksAS.ROCK_CRYSTAL_ORE.get().defaultBlockState(), false);
                        DataAS.DOMAIN_AS.getData(worldgenlevel.getLevel(), DataAS.KEY_ROCK_CRYSTAL_DATA).addOre(blockpos);
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
