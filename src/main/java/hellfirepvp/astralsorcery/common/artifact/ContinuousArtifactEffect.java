/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.artifact;

import hellfirepvp.astralsorcery.common.entity.item.ItemEntityArtifact;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ContinuousArtifactEffect
 * Created by HellFirePvP
 * Date: 24.10.2026 / 19:22
 */
public interface ContinuousArtifactEffect {

    void tickEffect(RandomSource rand, ServerLevel sLevel, ItemEntityArtifact artifact);

}
