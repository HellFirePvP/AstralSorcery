/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.storage;

import hellfirepvp.astralsorcery.common.data.world.StorageNetworkBuffer;
import hellfirepvp.astralsorcery.common.lib.DataAS;
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
 * Date: 30.05.2019 / 15:02
 */
public class StorageNetworkHandler {

    //private static final AxisAlignedBB box = new AxisAlignedBB(-3, 0, -3, 3, 0, 3);
    private static Map<Integer, NetworkHelper> mappingHelpers = new HashMap<>();

    public static NetworkHelper getHandler(World world) {
        return mappingHelpers.computeIfAbsent(world.getDimension().getType().getId(), id -> new NetworkHelper(world));
    }

    public static void clearHandler(World world) {
        clearHandler(world.getDimension().getType().getId());
    }

    public static void clearHandler(int dimId) {
        mappingHelpers.remove(dimId);
    }

    public static class NetworkHelper {

        private final StorageNetworkBuffer buffer;

        private NetworkHelper(World world) {
            this.buffer = DataAS.DOMAIN_AS.getData(world, DataAS.KEY_STORAGE_NETWORK);
        }

        @Nullable
        public StorageNetwork getNetwork(BlockPos networkMaster) {
            return buffer.getNetwork(networkMaster);
        }

        //public void addCore(TileStorageCore core) {
        //    //fusion logic
        //}

        //public void removeCore(TileStorageCore core) {
        //    //division logic
        //}

    }

    public static class MappingChange {

    }

}
