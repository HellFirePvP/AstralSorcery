/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon.data;

import hellfirepvp.astralsorcery.common.base.patreon.data.provider.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonEffectType
 * Created by HellFirePvP
 * Date: 16.02.2019 / 17:33
 */
public enum PatreonEffectType {

    FLARE(new FlareEffectProvider()),
    FLARE_DYNAMIC_COLOR(new DynFlareEffectProvider()),
    FLARE_CRYSTAL(new FloatingFlareCrystalProvider()),
    TREE_BEACON_COLOR(new TreeBeaconEffectProvider()),
    HELMET(new HelmetRenderProvider()),
    CRYSTAL_FOOTPRINTS(new CrystalFootprintProvider()),
    BLOCK_RING(new BlockRingProvider()),

    CORRUPTED_CRYSTAL(new CorruptedCelestialCrystalProvider()),
    FLOATING_HALO(new SpecificPatreonHaloEffectProvider()),
    CELESTIAL_WINGS(new CelestialWingsProvider());

    private final EffectProvider<?> provider;

    PatreonEffectType(EffectProvider<?> provider) {
        this.provider = provider;
    }

    public EffectProvider<?> getProvider() {
        return provider;
    }

}
