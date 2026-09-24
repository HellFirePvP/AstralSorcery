/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StarlightTransmissionLevelHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StarlightTransmissionLevelHelper {

    private static final StarlightTransmissionLevelHelper INSTANCE = new StarlightTransmissionLevelHelper();
    private final Map<ResourceKey<Level>, StarlightTransmissionLevelHandler> levelHandlers = new HashMap<>();

    private StarlightTransmissionLevelHelper() {}

    public static StarlightTransmissionLevelHelper getInstance() {
        return INSTANCE;
    }

    public void attachEventListeners(IEventBus eventBus) {
        eventBus.addListener(this::onLevelTick);
        eventBus.addListener(this::onLevelUnload);
        eventBus.addListener(this::onChunkLoad);
        eventBus.addListener(this::onChunkUnload);
    }

    private void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel sLevel) {
            this.levelHandlers.computeIfAbsent(sLevel.dimension(), StarlightTransmissionLevelHandler::new).tick(sLevel);
        }
    }

    private void onLevelUnload(LevelEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel sLevel)) return;
        ResourceKey<Level> dimKey = sLevel.dimension();
        StarlightTransmissionLevelHandler handle = this.levelHandlers.remove(dimKey);
        if (handle != null) {
            handle.clear();
        }
    }

    private void onChunkLoad(ChunkEvent.Load event) {
        if (event.getLevel() instanceof ServerLevel sLevel) {
            this.getHandler(sLevel).ifPresent(handler ->
                    handler.onChunkLoad(event.getChunk().getPos()));
        }
    }

    private void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel sLevel) {
            this.getHandler(sLevel).ifPresent(handler ->
                    handler.onChunkUnload(event.getChunk().getPos()));
        }
    }

    public Optional<StarlightTransmissionLevelHandler> getHandler(ServerLevel level) {
        return Optional.ofNullable(this.levelHandlers.get(level.dimension()));
    }

    public void clearServer() {
        this.levelHandlers.values().forEach(StarlightTransmissionLevelHandler::clear);
        this.levelHandlers.clear();
    }
}
