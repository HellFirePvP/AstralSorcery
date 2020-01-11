/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.world;

import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.client.PktRequestSeed;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: WorldSeedCache
 * Created by HellFirePvP
 * Date: 02.06.2019 / 14:21
 */
public class WorldSeedCache {

    private static int activeSession = 0;

    private static Map<DimensionType, Long> cacheSeedLookup = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    public static void clearClient() {
        activeSession++;
        cacheSeedLookup.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public static void updateSeedCache(DimensionType type, int session, long seed) {
        if (activeSession == session) {
            cacheSeedLookup.put(type, seed);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static Optional<Long> getSeedIfPresent(World world) {
        if (world == null) return Optional.empty();
        return getSeedIfPresent(world.getDimension().getType());
    }

    @OnlyIn(Dist.CLIENT)
    public static Optional<Long> getSeedIfPresent(DimensionType type) {
        if (!cacheSeedLookup.containsKey(type)) {
            activeSession++;
            PktRequestSeed req = new PktRequestSeed(activeSession, type);
            PacketChannel.CHANNEL.sendToServer(req);
            return Optional.empty();
        }
        return Optional.of(cacheSeedLookup.get(type));
    }

}
