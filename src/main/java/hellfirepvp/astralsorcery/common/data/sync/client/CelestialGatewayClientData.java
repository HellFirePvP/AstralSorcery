/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.client;

import hellfirepvp.astralsorcery.common.data.level.CelestialGatewayData;
import hellfirepvp.astralsorcery.common.data.sync.ClientData;
import hellfirepvp.astralsorcery.common.util.data.DiffEntry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CelestialGatewayClientData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CelestialGatewayClientData extends ClientData {

    private final Map<ResourceKey<Level>, Map<BlockPos, CelestialGatewayData.GatewayEntry>> levelGatewayEntries = new HashMap<>();

    public void receiveAll(Map<ResourceKey<Level>, Set<CelestialGatewayData.GatewayEntry>> allEntries) {
        this.levelGatewayEntries.clear();
        allEntries.forEach((levelKey, newEntries) -> {
            var entries = this.levelGatewayEntries.computeIfAbsent(levelKey, k -> new HashMap<>());
            newEntries.forEach(entry -> entries.put(entry.getPos(), entry));
        });
    }

    public void receiveChanges(Set<ResourceKey<Level>> removedLevels, Map<ResourceKey<Level>, List<DiffEntry<CelestialGatewayData.GatewayEntry>>> entryChanges) {
        removedLevels.forEach(this.levelGatewayEntries::remove);

        entryChanges.forEach((levelKey, diffEntries) -> {
            var entries = this.levelGatewayEntries.computeIfAbsent(levelKey, k -> new HashMap<>());
            diffEntries.forEach(entry -> {
                switch (entry.type()) {
                    case ADDITION -> entries.put(entry.value().getPos(), entry.value());
                    case REMOVAL -> entries.remove(entry.value().getPos());
                }
            });
        });
    }

    public Collection<ResourceKey<Level>> getKnownLevels() {
        return Collections.unmodifiableSet(this.levelGatewayEntries.keySet());
    }

    public Map<BlockPos, CelestialGatewayData.GatewayEntry> getEntries(ResourceKey<Level> levelKey) {
        return this.levelGatewayEntries.getOrDefault(levelKey, Collections.emptyMap());
    }

    public Optional<CelestialGatewayData.GatewayEntry> getEntry(ResourceKey<Level> levelKey, BlockPos pos) {
        return Optional.ofNullable(this.getEntries(levelKey).get(pos));
    }

    @Override
    public void clear() {
        this.levelGatewayEntries.clear();
    }
}
