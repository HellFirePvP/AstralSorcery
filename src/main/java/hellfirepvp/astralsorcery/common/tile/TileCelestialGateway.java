package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.GatewayCache;
import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileCelestialGateway
 * Created by HellFirePvP
 * Date: 16.04.2017 / 20:43
 */
public class TileCelestialGateway extends TileEntityTick {

    @Override
    protected void onFirstTick() {
        GatewayCache cache = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.GATEWAY_DATA);
        cache.offerPosition(world, pos);
    }

}
