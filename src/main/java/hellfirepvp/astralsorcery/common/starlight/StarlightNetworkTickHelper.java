/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight;

import hellfirepvp.astralsorcery.common.starlight.api.ITransmissionTickable;
import hellfirepvp.astralsorcery.common.starlight.api.TransmissionNode;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StarlightNetworkTickHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StarlightNetworkTickHelper {

    private static final StarlightNetworkTickHelper INSTANCE = new StarlightNetworkTickHelper();

    private final Map<ResourceKey<Level>, List<ITransmissionTickable>> nodeUpdates = new HashMap<>();
    private static final Object accessLock = new Object();

    private StarlightNetworkTickHelper() {}

    public static StarlightNetworkTickHelper getInstance() {
        return INSTANCE;
    }

    public void attachEventListeners(IEventBus bus) {
        bus.addListener(this::onLevelTick);
        bus.addListener(this::onWorldLoad);
        bus.addListener(this::onWorldUnload);
    }

    private void onLevelTick(LevelTickEvent.Post event) {
        Level level = event.getLevel();
        if (level.isClientSide()) return;
        ResourceKey<Level> dimKey = level.dimension();

        synchronized (accessLock) {
            this.nodeUpdates.getOrDefault(dimKey, Collections.emptyList()).forEach(node -> {
                node.update(level);
            });
        }
    }

    public void addNodeUpdate(Level level, ITransmissionTickable node) {
        synchronized (accessLock) {
            this.nodeUpdates.computeIfAbsent(level.dimension(), k -> new ArrayList<>()).add(node);
        }
    }

    public void removeNodeUpdate(Level level, ITransmissionTickable node) {
        synchronized (accessLock) {
            this.nodeUpdates.getOrDefault(level.dimension(), Collections.emptyList()).remove(node);
        }
    }

    private void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof Level level) {
            synchronized (accessLock) {
                this.nodeUpdates.remove(level.dimension());
            }
        }
    }

    private void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            synchronized (accessLock) {
                this.nodeUpdates.remove(level.dimension());
            }
        }
    }
}
