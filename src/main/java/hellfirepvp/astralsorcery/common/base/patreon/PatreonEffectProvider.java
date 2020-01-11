/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon;

import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonEffectProvider
 * Created by HellFirePvP
 * Date: 30.08.2019 / 23:22
 */
public interface PatreonEffectProvider<T extends PatreonEffect> {

    public T buildEffect(UUID playerUUID, List<String> effectParameters) throws Exception;

}
