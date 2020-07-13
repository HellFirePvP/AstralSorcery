/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.world.GatewayCache;
import hellfirepvp.astralsorcery.common.data.world.LightNetworkBuffer;
import hellfirepvp.astralsorcery.common.data.world.RockCrystalBuffer;
import hellfirepvp.astralsorcery.common.data.world.StorageNetworkBuffer;
import hellfirepvp.observerlib.common.data.WorldCacheManager;

import static hellfirepvp.astralsorcery.common.lib.DataAS.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: RegistryData
 * Created by HellFirePvP
 * Date: 03.07.2019 / 16:47
 */
public class RegistryData {

    private RegistryData() {}

    public static void init() {
        DOMAIN_AS = WorldCacheManager.createDomain(AstralSorcery.MODID);

        KEY_GATEWAY_CACHE = DOMAIN_AS.createSaveKey("gateway-cache", GatewayCache::new);
        KEY_STARLIGHT_NETWORK = DOMAIN_AS.createSaveKey("lightnetwork", LightNetworkBuffer::new);
        KEY_STORAGE_NETWORK = DOMAIN_AS.createSaveKey("storagenetwork", StorageNetworkBuffer::new);
        KEY_ROCK_CRYSTAL_BUFFER = DOMAIN_AS.createSaveKey("rock-crystals", RockCrystalBuffer::new);
    }

}
