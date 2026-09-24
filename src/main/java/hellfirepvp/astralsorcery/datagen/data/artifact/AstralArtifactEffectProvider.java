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
import hellfirepvp.astralsorcery.common.util.data.IntRange;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.MobCategory;

import java.util.concurrent.CompletableFuture;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AstralArtifactEffectProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AstralArtifactEffectProvider extends ArtifactEffectProvider {

    public AstralArtifactEffectProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(AstralSorcery.MODID, true, output, registries);
    }

    @Override
    public void registerEffects() {
        this.newCondition("random_animals")
                .build(this.spawnNaturalMobs(6, IntRange.of(5, 10), MobCategory.CREATURE));
        this.newCondition("effect_regeneration")
                .build(this.applyEffects()
                        .addEffect(MobEffects.REGENERATION, 1000, 1)
                        .addEffect(MobEffects.SATURATION, 1000, 1)
                        .build());
        this.newCondition("effect_speed")
                .build(this.applyEffects()
                        .addEffect(MobEffects.DIG_SPEED, 1000, 2)
                        .addEffect(MobEffects.MOVEMENT_SPEED, 1000)
                        .build());
        this.newCondition("effect_resistance")
                .build(this.applyEffects()
                        .addEffect(MobEffects.DAMAGE_RESISTANCE, 1000)
                        .addEffect(MobEffects.FIRE_RESISTANCE, 1000)
                        .build());
    }
}
