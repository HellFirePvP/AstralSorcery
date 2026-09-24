/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.patreon;

import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatreonEffectProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface PatreonEffectProvider<T extends PatreonEffect> {

    T buildEffect(UUID playerUUID, List<String> parameters) throws Exception;

    default UUID getEffectUUID(List<String> parameters) throws Exception {
        return UUID.fromString(parameters.getFirst());
    }
}
