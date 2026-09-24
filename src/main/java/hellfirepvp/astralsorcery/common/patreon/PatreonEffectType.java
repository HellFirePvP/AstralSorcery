/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.patreon;

import hellfirepvp.astralsorcery.common.patreon.type.provider.TypeFlareProvider;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatreonEffectType
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public enum PatreonEffectType {

    FLARE(new TypeFlareProvider());

    private final PatreonEffectProvider<?> provider;

    PatreonEffectType(PatreonEffectProvider<?> provider) {
        this.provider = provider;
    }

    PatreonEffectProvider<?> getProvider() {
        return this.provider;
    }
}
