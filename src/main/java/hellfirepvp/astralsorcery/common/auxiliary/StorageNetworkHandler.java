/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary;

import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.StorageNetworkBuffer;
import hellfirepvp.astralsorcery.common.tile.TileStorageCore;
import hellfirepvp.astralsorcery.common.tile.storage.StorageNetwork;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StorageNetworkHandler
 * Created by HellFirePvP
 * Date: 13.12.2017 / 20:19
 */
public class StorageNetworkHandler {

    //private static final AxisAlignedBB box = new AxisAlignedBB(-3, 0, -3, 3, 0, 3);
    private static Map<Integer, NetworkHelper> mappingHelpers = new HashMap<>();

    public static NetworkHelper getHandler(World world) {
        return mappingHelpers.computeIfAbsent(world.provider.getDimension(), id -> new NetworkHelper(world));
    }

    public static void clearHandler(World world) {
        clearHandler(world.provider.getDimension());
    }

    public static void clearHandler(int dimId) {
        mappingHelpers.remove(dimId);
    }

    public static class NetworkHelper {

        private StorageNetworkBuffer buffer;

        private NetworkHelper(World world) {
            this.buffer = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.STORAGE_BUFFER);
        }

        @Nullable
        public StorageNetwork getNetwork(BlockPos networkMaster) {
            return buffer.getNetwork(networkMaster);
        }

        public void addCore(TileStorageCore core) {
            //TODO fusion logic
        }

        public void removeCore(TileStorageCore core) {
            //TODO division logic
        }

    }

    public static class MappingChange {

    }

}
