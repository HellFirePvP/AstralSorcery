/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.patreon;

import hellfirepvp.astralsorcery.client.config.RenderingConfig;
import hellfirepvp.astralsorcery.common.util.data.MapStream;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatreonEffectHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PatreonEffectHelper {

    static boolean loadingFinished = false;
    static Map<UUID, List<PatreonEffect>> playerEffectMap = new HashMap<>();
    static Map<UUID, PatreonEffect> effectMap = new HashMap<>();

    @Nullable
    public static PatreonEffect getEffect(UUID effectUUID) {
        return effectMap.get(effectUUID);
    }

    public static List<PatreonEffect> getPatreonEffects(LogicalSide side, UUID playerUUID) {
        if (side.isClient() && !RenderingConfig.CONFIG.patreonEffects.get()) {
            return Collections.emptyList();
        }
        if (!loadingFinished) {
            return Collections.emptyList();
        }
        return playerEffectMap.getOrDefault(playerUUID, Collections.emptyList());
    }

    public static Map<UUID, List<PatreonEffect>> getPatreonEffects(Collection<? extends Player> players) {
        if (!loadingFinished) {
            return new HashMap<>();
        }
        Collection<UUID> playerUUIDs = players.stream().map(Entity::getUUID).toList();
        return MapStream.of(playerEffectMap)
                .filterKey(playerUUIDs::contains)
                .toMap();
    }
}
