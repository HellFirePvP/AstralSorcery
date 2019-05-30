/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.world;

import net.minecraft.world.World;

import java.util.Random;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CachedWorldData
 * Created by HellFirePvP
 * Date: 02.08.2016 / 23:21
 */
public abstract class CachedWorldData implements IWorldRelatedData {

    protected final Random rand = new Random();
    private final WorldCacheManager.SaveKey key;

    protected CachedWorldData(WorldCacheManager.SaveKey key) {
        this.key = key;
    }

    public abstract boolean needsSaving();

    public abstract void markSaved();

    public abstract void updateTick(World world);

    public final WorldCacheManager.SaveKey getSaveKey() {
        return key;
    }

    public void onLoad(World world) {}

}
