/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;

import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.StorageNetworkBuffer;
import hellfirepvp.astralsorcery.common.tile.IStorageNetworkTile;
import hellfirepvp.astralsorcery.common.tile.TileStorageCore;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StorageNetworkHandler
 * Created by HellFirePvP
 * Date: 13.12.2017 / 20:19
 */
public class StorageNetworkHandler {

    private static final AxisAlignedBB box = new AxisAlignedBB(-3, 0, -3, 3, 0, 3);
    private static Map<Integer, MappingHandler> storageClusters = new HashMap<>();

    public static MappingHandler getHandler(World world) {
        return storageClusters.computeIfAbsent(world.provider.getDimension(), id -> new MappingHandler());
    }

    public static void clearHandler(World world) {
        clearHandler(world.provider.getDimension());
    }

    public static void clearHandler(int dimId) {
        storageClusters.remove(dimId);
    }

    public static class MappingHandler {

        private List<StorageNetwork> clusters = new LinkedList<>();

        public void addCore(TileStorageCore core) {
            //StorageNetworkBuffer net = WorldCacheManager.getOrLoadData(core.getWorld(),
            //        WorldCacheManager.SaveKey.STORAGE_BUFFER);
            //net.add(core);

            //TODO fusion logic
        }

        public void removeCore(TileStorageCore core) {
            //StorageNetworkBuffer net = WorldCacheManager.getOrLoadData(core.getWorld(),
            //        WorldCacheManager.SaveKey.STORAGE_BUFFER);
            //net.remove(core);

            //TODO division logic
        }

    }

    public static class MappingChange {

        public List<TileStorageCore> previousCores = new LinkedList<>();
        public Map<TileStorageCore.StorageKey, TileStorageCore.StorageCache> contents = new HashMap<>();

    }

    public static class StorageNetwork {

        private TileStorageCore master = null;
        private List<TileStorageCore> cores = new LinkedList<>();

    }

}
