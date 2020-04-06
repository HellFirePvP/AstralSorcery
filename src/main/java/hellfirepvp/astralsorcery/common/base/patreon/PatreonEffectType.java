/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon;

import hellfirepvp.astralsorcery.common.base.patreon.types.provider.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonEffectType
 * Created by HellFirePvP
 * Date: 30.08.2019 / 23:22
 */
public enum PatreonEffectType {

    FLARE(new ProviderFlare()),

    FLARE_DYNAMIC_COLOR(new ProviderFlareDynamicColor()),
    FLARE_CRYSTAL(new ProviderFlareCrystal()),
    CRYSTAL_FOOTPRINTS(new ProviderCrystalFootprints()),
    HELMET(new ProviderHelmetRender()),
    WRAITH_WINGS(new ProviderWraithWings()),
    CELESTIAL_WINGS(new ProviderCelestialWings()),
    BLOCK_RING(new ProviderBlockRing());

    private final PatreonEffectProvider<?> provider;

    PatreonEffectType(PatreonEffectProvider<?> provider) {
        this.provider = provider;
    }

    public PatreonEffectProvider<?> getProvider() {
        return provider;
    }
}
