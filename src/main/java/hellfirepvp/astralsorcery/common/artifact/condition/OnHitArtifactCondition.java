/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact.condition;

import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: OnHitArtifactCondition
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface OnHitArtifactCondition {

    boolean isFulfilled(ItemEntityArtifact artifact, RandomSource rand, DamageSource source, float amount);

}
