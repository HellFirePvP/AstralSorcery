package hellfirepvp.astralsorcery.common.data.world;

import hellfirepvp.astralsorcery.common.data.world.data.LightNetworkBuffer;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldCacheManager
 * Created by HellFirePvP
 * Date: 02.08.2016 / 23:15
 */
public class WorldCacheManager {

    public static <T extends CachedWorldData> T getData(World world, SaveKey key) {
        return key.getDummyObject().initializeAndGet(world);
    }

    public static void informTick(World world) {
        for (SaveKey key : SaveKey.values()) {
            key.getDummyObject().initializeAndGet(world).updateTick();
        }
    }

    public static enum SaveKey {

        ROCK_CRYSTAL("astralsorcery:rcrystals", new RockCrystalBuffer()),
        LIGHT_NETWORK("astralsorcery:lightnetwork", new LightNetworkBuffer());

        private final String identifier;
        private final CachedWorldData dummyObject;

        private SaveKey(String identifier, CachedWorldData dummyObject) {
            this.identifier = identifier;
            this.dummyObject = dummyObject;
        }

        public CachedWorldData getDummyObject() {
            return dummyObject;
        }

        public String getIdentifier() {
            return identifier;
        }

    }

}
