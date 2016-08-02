package hellfirepvp.astralsorcery.common.starlight;

import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.LightNetworkBuffer;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldNetworkHandler
 * Created by HellFirePvP
 * Date: 02.08.2016 / 23:09
 */
public class WorldNetworkHandler {
    
    private final LightNetworkBuffer buffer;

    public WorldNetworkHandler(LightNetworkBuffer lightNetworkBuffer) {
        this.buffer = lightNetworkBuffer;
    }

    public static WorldNetworkHandler getNetworkHandler(World world) {
        LightNetworkBuffer buffer = WorldCacheManager.getData(world, WorldCacheManager.SaveKey.LIGHT_NETWORK);
        return buffer.getNetworkHandler();
    }

}
