/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.datagen.data.artifact;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.artifact.effect.data.ArtifactEffectProvider;
import hellfirepvp.astralsorcery.common.util.data.FloatRange;
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.MobCategory;

import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralArtifactPenaltyProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralArtifactPenaltyProvider extends ArtifactEffectProvider {

    public AstralArtifactPenaltyProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(AstralSorcery.MODID, false, output, registries);
    }

    @Override
    public void registerEffects() {
        this.newCondition("random_move")
                .build(this.randomMove(FloatRange.of(5, 10), 0.1F));
        this.newCondition("random_teleport")
                .build(this.randomTeleport(FloatRange.of(5, 10)));
        this.newCondition("random_mob_spawns")
                .build(this.spawnNaturalMobs(7, IntRange.of(8, 13), MobCategory.MONSTER));
    }
}
