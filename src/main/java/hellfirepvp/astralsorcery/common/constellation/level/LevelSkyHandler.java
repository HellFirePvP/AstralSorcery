/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.level;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.util.level.LevelEffectSeedCache;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LevelSkyHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LevelSkyHandler {

    private static final LevelSkyHandler INSTANCE = new LevelSkyHandler();

    private final Map<ResourceKey<Level>, LevelSkyContext> worldHandlersServer = Maps.newHashMap();
    private final Map<ResourceKey<Level>, LevelSkyContext> worldHandlersClient = Maps.newHashMap();

    private LevelSkyHandler() {}

    public static LevelSkyHandler getInstance() {
        return INSTANCE;
    }

    public void attachEventListeners(IEventBus bus) {
        bus.addListener(this::onWorldTick);
        bus.addListener(this::onClientTick);
        bus.addListener(this::onWorldUnload);
    }

    private void onWorldTick(LevelTickEvent.Pre event) {
        Level level = event.getLevel();
        if (level instanceof ServerLevel) {
            ResourceKey<Level> dimKey = level.dimension();

            LevelSkyContext ctx = worldHandlersServer.computeIfAbsent(dimKey,
                    key -> createContext(LevelEffectSeedCache.getServerWorldSeed(key)));
            ctx.tick(level);
        }
    }

    private void onClientTick(ClientTickEvent.Pre event) {
        this.sidedClientTick();
    }

    @OnlyIn(Dist.CLIENT)
    private void sidedClientTick() {
        Level level = Minecraft.getInstance().level;
        if (level != null) {
            ResourceKey<Level> dimKey = level.dimension();

            LevelSkyContext ctx = worldHandlersClient.get(dimKey);
            if (ctx == null) {
                Optional<Long> seedOpt = LevelEffectSeedCache.getClientSeedIfPresent(dimKey);
                if (seedOpt.isEmpty()) return;

                ctx = createContext(seedOpt.get());
                worldHandlersClient.put(dimKey, ctx);
            }
            ctx.tick(level);
        }
    }

    public void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            worldHandlersServer.remove(level.dimension());
            worldHandlersClient.remove(level.dimension());
        }
    }

    public void clientClearCache() {
        worldHandlersClient.clear();
    }

    private LevelSkyContext createContext(long seed) {
        return new LevelSkyContext(seed);
    }

    public static Optional<LevelSkyContext> getContext(Level level) {
        return getContext(level, level.isClientSide() ? LogicalSide.CLIENT : LogicalSide.SERVER);
    }

    public static Optional<LevelSkyContext> getContext(Level level, LogicalSide dist) {
        if (level == null) return Optional.empty();
        ResourceKey<Level> dimKey = level.dimension();
        if (dist.isClient()) {
            return Optional.ofNullable(getInstance().worldHandlersClient.get(dimKey));
        } else {
            return Optional.ofNullable(getInstance().worldHandlersServer.get(dimKey));
        }
    }
}
