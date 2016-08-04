package hellfirepvp.astralsorcery.common.data.world;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.data.world.data.LightNetworkBuffer;
import hellfirepvp.astralsorcery.common.data.world.data.RockCrystalBuffer;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Constructor;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldCacheManager
 * Created by HellFirePvP
 * Date: 02.08.2016 / 23:15
 */
public class WorldCacheManager implements ITickHandler {

    //initializeAndGet is in O(1) - i admit, that's not obvious.
    public static <T extends CachedWorldData> T getData(World world, SaveKey key) {
        return key.getDummyObject().initializeAndGet(world);
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        World world = (World) context[0];
        if(world.isRemote) return;

        for (SaveKey key : SaveKey.values()) {
            key.getDummyObject().initializeAndGet(world).updateTick(world);
        }
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.WORLD);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "WorldCacheManager";
    }

    public static enum SaveKey {

        ROCK_CRYSTAL("astralsorcery:rcrystals", RockCrystalBuffer.class),
        LIGHT_NETWORK("astralsorcery:lightnetwork", LightNetworkBuffer.class);

        private final String identifier;
        private final Class<? extends CachedWorldData> clazz;
        private CachedWorldData dummyObject;

        private static Map<String, SaveKey> keyMap = new HashMap<>();

        private SaveKey(String identifier, Class<? extends CachedWorldData> clazz) {
            this.identifier = identifier;
            this.clazz = clazz;
        }

        public CachedWorldData getDummyObject() {
            if(dummyObject == null) {
                try {
                    Constructor<? extends CachedWorldData> ctor = clazz.getDeclaredConstructor();
                    ctor.setAccessible(true);
                    dummyObject = ctor.newInstance();
                } catch (Exception e) {
                    AstralSorcery.log.info("Couldn't initialize WorldData for " + identifier);
                    return null;
                }
            }
            return dummyObject;
        }

        public String getIdentifier() {
            return identifier;
        }

        public static SaveKey getByIdentifier(String identifier) {
            return keyMap.get(identifier);
        }

        static {
            for (SaveKey key : values()) {
                keyMap.put(key.identifier, key);
            }
        }

    }

}
