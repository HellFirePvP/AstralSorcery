package hellfirepvp.astralsorcery.common.data.world;

import hellfirepvp.astralsorcery.AstralSorcery;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CachedWorldData
 * Created by HellFirePvP
 * Date: 02.08.2016 / 23:21
 */
public abstract class CachedWorldData extends WorldSavedData {

    private final WorldCacheManager.SaveKey key;

    //Because minecraft.
    public CachedWorldData(String key) {
        super(key);
        this.key = WorldCacheManager.SaveKey.getByIdentifier(key);
        if(this.key == null) {
            AstralSorcery.log.info("Unknown data identifier: " + key + " but resulted in AstralSorcery's CachedWorldData. Ignoring key...");
        }
    }

    public CachedWorldData(WorldCacheManager.SaveKey key) {
        super(key.getIdentifier());
        this.key = key;
    }

    public abstract <T extends CachedWorldData> T constructNewData();

    public abstract void updateTick();

    public final WorldCacheManager.SaveKey getSaveKey() {
        return key;
    }

    //in O(1)
    public final  <T extends CachedWorldData> T initializeAndGet(World world) {
        String id = getSaveKey().getIdentifier();
        CachedWorldData data = (CachedWorldData) world.loadItemData(getClass(), id);
        if (data == null) {
            data = constructNewData();
            world.setItemData(id, data);
        }
        return (T) data;
    }

}
