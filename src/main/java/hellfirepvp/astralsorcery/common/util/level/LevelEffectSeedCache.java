/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.level;

import hellfirepvp.astralsorcery.common.network.play.PktRequestSeed;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LevelEffectSeedCache
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LevelEffectSeedCache {

    private static long lastServerQuery = 0L;
    private static int activeSession = 0;

    private static final Map<ResourceKey<Level>, Long> clientSeedLookup = new HashMap<>();
    private static final Map<ResourceKey<Level>, Long> serverSeedLookup = new HashMap<>();

    public static long getServerWorldSeed(ResourceKey<Level> dim) {
        if (serverSeedLookup.containsKey(dim)) return serverSeedLookup.get(dim);
        MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
        if (srv == null) return -1;
        ServerLevel level = srv.getLevel(dim);
        if (level == null) return -1;
        long randVal = RandomSource.create(level.getSeed()).nextLong();
        serverSeedLookup.put(dim, randVal);
        return randVal;
    }

    @OnlyIn(Dist.CLIENT)
    public static void clearClient() {
        activeSession++;
        clientSeedLookup.clear();
    }

    @OnlyIn(Dist.CLIENT)
    public static void updateClientSeedCache(ResourceKey<Level> dim, int session, long seed) {
        if (activeSession == session) {
            clientSeedLookup.put(dim, seed);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static Optional<Long> getClientSeedIfPresent(ResourceKey<Level> dim) {
        if (dim == null) {
            return Optional.empty();
        }
        if (!clientSeedLookup.containsKey(dim)) {
            long current = System.currentTimeMillis();
            if (current - lastServerQuery > 5_000) {
                lastServerQuery = current;
                activeSession++;

                PacketDistributor.sendToServer(PktRequestSeed.newRequest(dim, activeSession));
            }
            return Optional.empty();
        }
        return Optional.of(clientSeedLookup.get(dim));
    }
}
