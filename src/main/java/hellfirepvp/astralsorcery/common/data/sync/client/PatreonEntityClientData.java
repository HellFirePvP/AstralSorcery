/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.client;

import hellfirepvp.astralsorcery.common.data.sync.ClientData;
import hellfirepvp.astralsorcery.common.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.patreon.PatreonEffectHelper;
import hellfirepvp.astralsorcery.common.patreon.entity.PatreonPartialEntity;
import net.neoforged.fml.LogicalSide;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatreonEntityClientData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PatreonEntityClientData extends ClientData {

    private final Map<UUID, Set<PatreonPartialEntity>> clientEntities = new HashMap<>();

    public void receiveChanges(Set<UUID> removals, Map<UUID, Map<UUID, PatreonPartialEntity.UpdateInfo>> flareUpdateMap) {
        flareUpdateMap.forEach((playerUUID, entities) -> {
            Set<PatreonPartialEntity> existingEntities = this.clientEntities.computeIfAbsent(playerUUID, id -> new HashSet<>());

            entities.forEach((effectUUID, entityUpdateInfo) -> {
                PatreonPartialEntity existingEntity = existingEntities.stream()
                        .filter(e -> e.getEffectUUID().equals(effectUUID))
                        .findFirst()
                        .orElse(null);

                if (existingEntity == null) {
                    PatreonEffect pe = PatreonEffectHelper.getPatreonEffects(LogicalSide.CLIENT, playerUUID).stream()
                            .filter(effect -> effect.getEffectUUID().equals(effectUUID))
                            .findFirst()
                            .orElse(null);
                    if (pe == null) return;

                    PatreonPartialEntity.Provider provider = pe.getPartialEntityProvider();
                    if (provider == null) return; // patreon file mismatch?

                    PatreonPartialEntity entity = provider.clientProvider().apply(playerUUID);
                    if (entity == null) return;

                    entity.applyUpdateInfo(entityUpdateInfo);

                    existingEntities.add(entity);
                } else {
                    existingEntity.applyUpdateInfo(entityUpdateInfo);
                }
            });
        });

        removals.forEach(this.clientEntities::remove);
    }

    @Override
    public void clear() {
        this.clientEntities.clear();
    }

    public List<Set<PatreonPartialEntity>> getAllEntities() {
        return new ArrayList<>(this.clientEntities.values());
    }
}
