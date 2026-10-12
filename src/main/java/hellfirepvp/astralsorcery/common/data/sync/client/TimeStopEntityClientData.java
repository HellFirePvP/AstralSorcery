/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.client;

import hellfirepvp.astralsorcery.common.data.sync.ClientData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TimeStopEntityClientData
 * Created by HellFirePvP
 * Date: 09.10.2026 / 13:55
 */
public class TimeStopEntityClientData extends ClientData {

    private final Map<ResourceKey<Level>, Set<Integer>> clientEntityFreeze = new HashMap<>();

    public boolean isFrozen(Entity entity) {
        return this.clientEntityFreeze.getOrDefault(entity.level().dimension(), Collections.emptySet()).contains(entity.getId());
    }

    public void syncAll(Map<ResourceKey<Level>, Set<Integer>> entities) {
        this.clientEntityFreeze.clear();

        entities.forEach((key, ids) -> {
            this.clientEntityFreeze.computeIfAbsent(key, k -> new HashSet<>()).addAll(ids);
        });
    }

    public void syncDiff(Map<ResourceKey<Level>, Map<Integer, Boolean>> updates) {
        updates.forEach((key, idUpdates) -> {
            Set<Integer> ids = this.clientEntityFreeze.computeIfAbsent(key, k -> new HashSet<>());
            idUpdates.forEach((id, isAdd) -> {
                if (isAdd) {
                    ids.add(id);
                } else {
                    ids.remove(id);
                }
            });
        });
    }

    @Override
    public void clear() {
        this.clientEntityFreeze.clear();
    }
}
