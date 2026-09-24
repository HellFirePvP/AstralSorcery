/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.level.*;
import hellfirepvp.observerlib.common.data.WorldCacheDomain;
import hellfirepvp.observerlib.common.data.WorldCacheManager;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DataAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DataAS {

    public static final WorldCacheDomain DOMAIN_AS = WorldCacheManager.createDomain(AstralSorcery.MODID);

    public static final WorldCacheDomain.SaveKey<RockCrystalData> KEY_ROCK_CRYSTAL_DATA =
            DOMAIN_AS.createSaveKey("rock_crystals", RockCrystalData.CODEC, RockCrystalData::new);
    public static final WorldCacheDomain.SaveKey<StarlightNetworkData> KEY_STARLIGHT_NETWORK_DATA =
            DOMAIN_AS.createSaveKey("starlight_network", StarlightNetworkData.CODEC, StarlightNetworkData::new);
    public static final WorldCacheDomain.SaveKey<FocalPointData> KEY_FOCAL_POINT_DATA =
            DOMAIN_AS.createSaveKey("focal_points", FocalPointData.CODEC, FocalPointData::new);
    public static final WorldCacheDomain.SaveKey<LumenNetworkData> KEY_LUMEN_NETWORK_DATA =
            DOMAIN_AS.createSaveKey("lumen_network", LumenNetworkData.CODEC, LumenNetworkData::new);
    public static final WorldCacheDomain.SaveKey<CelestialGatewayData> KEY_CELESTIAL_GATEWAY_DATA =
            DOMAIN_AS.createSaveKey("celestial_gateways", CelestialGatewayData.CODEC, CelestialGatewayData::new);

    public static void init() {}

}
