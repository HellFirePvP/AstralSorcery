/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.client;

import hellfirepvp.astralsorcery.common.data.sync.ClientData;
import hellfirepvp.astralsorcery.common.util.time.TimeStopZone;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TimeStopZoneClientData
 * Created by HellFirePvP
 * Date: 12.10.2026 / 22:26
 */
public class TimeStopZoneClientData extends ClientData {

    private final Map<ResourceKey<Level>, Set<TimeStopZone.Snapshot>> clientFreezeZones = new HashMap<>();

    public Set<TimeStopZone.Snapshot> getZones(Level level) {
        return this.clientFreezeZones.getOrDefault(level.dimension(), Collections.emptySet());
    }

    public void syncAll(Map<ResourceKey<Level>, Set<TimeStopZone.Snapshot>> allZones) {
        this.clientFreezeZones.clear();

        allZones.forEach((key, zones) -> {
            this.clientFreezeZones.computeIfAbsent(key, k -> new HashSet<>()).addAll(zones);
        });
    }

    public void syncDiff(Map<ResourceKey<Level>, Map<TimeStopZone.Snapshot, Boolean>> allZoneUpdates) {
        allZoneUpdates.forEach((key, zoneUpdates) -> {
            Set<TimeStopZone.Snapshot> zones = this.clientFreezeZones.computeIfAbsent(key, k -> new HashSet<>());
            zoneUpdates.forEach((snapshot, isAdd) -> {
                zones.removeIf(zone -> zone.id().equals(snapshot.id()));
                if (isAdd) {
                    zones.add(snapshot);
                }
            });
        });
    }

    @Override
    public void clear() {
        this.clientFreezeZones.clear();
    }
}
