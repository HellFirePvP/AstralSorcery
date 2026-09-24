/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.worldgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;

import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RockCrystalOreFeatureConfiguration
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RockCrystalOreFeatureConfiguration implements FeatureConfiguration {

    public static final Codec<RockCrystalOreFeatureConfiguration> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BlockPredicate.CODEC.fieldOf("replace_condition").forGetter(RockCrystalOreFeatureConfiguration::getReplaceCondition)
            ).apply(inst, RockCrystalOreFeatureConfiguration::new)
    );

    private final BlockPredicate replaceCondition;

    public RockCrystalOreFeatureConfiguration(BlockPredicate replaceCondition) {
        this.replaceCondition = replaceCondition;
    }

    public BlockPredicate getReplaceCondition() {
        return this.replaceCondition;
    }
}
