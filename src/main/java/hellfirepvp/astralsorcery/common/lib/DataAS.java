/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib;

import hellfirepvp.astralsorcery.common.data.world.GatewayCache;
import hellfirepvp.astralsorcery.common.data.world.LightNetworkBuffer;
import hellfirepvp.astralsorcery.common.data.world.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.data.world.StorageNetworkBuffer;
import hellfirepvp.observerlib.common.data.WorldCacheDomain;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataAS
 * Created by HellFirePvP
 * Date: 03.07.2019 / 16:46
 */
public class DataAS {

    private DataAS() {}

    public static WorldCacheDomain DOMAIN_AS;

    public static WorldCacheDomain.SaveKey<GatewayCache> KEY_GATEWAY_CACHE;
    public static WorldCacheDomain.SaveKey<LightNetworkBuffer> KEY_STARLIGHT_NETWORK;
    public static WorldCacheDomain.SaveKey<StorageNetworkBuffer> KEY_STORAGE_NETWORK;
    public static WorldCacheDomain.SaveKey<RockCrystalBuffer> KEY_ROCK_CRYSTAL_BUFFER;

}
