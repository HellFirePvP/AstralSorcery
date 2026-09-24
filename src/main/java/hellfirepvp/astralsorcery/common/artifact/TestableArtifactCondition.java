/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

import java.util.List;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TestableArtifactCondition
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record TestableArtifactCondition(ArtifactCondition condition, long conditionSeed, ChancedArtifactEffect successEffect) {

    public static final Codec<TestableArtifactCondition> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            ArtifactCondition.CODEC.fieldOf("condition").forGetter(r -> r.condition),
            Codec.LONG.fieldOf("conditionSeed").forGetter(r -> r.conditionSeed),
            ChancedArtifactEffect.CODEC.fieldOf("successEffect").forGetter(r -> r.successEffect)
    ).apply(inst, TestableArtifactCondition::new));

    public RandomSource getRandom() {
        return RandomSource.create(this.conditionSeed());
    }

    public ColorWrapper getColor() {
        RandomSource rand = this.getRandom();
        return ColorWrapper.ofHSB(rand.nextFloat(), 0.6F + rand.nextFloat() * 0.4F, 1F);
    }

    public List<Vector3> isFulfilled(ItemEntityArtifact artifactEntity) {
        if (!(artifactEntity.level() instanceof ServerLevel sLevel)) return List.of();
        return this.condition().isFulfilled(this.getRandom(), sLevel, artifactEntity);
    }

    public void fulfillCondition(ItemEntityArtifact artifactEntity) {
        if (!(artifactEntity.level() instanceof ServerLevel sLevel)) return;
        this.condition().fulfillCondition(this.getRandom(), sLevel, artifactEntity);
    }

    public Component getDisplay(boolean wasSuccessful) {
        return this.condition().getDisplayHint(this.getRandom(), wasSuccessful);
    }
}
