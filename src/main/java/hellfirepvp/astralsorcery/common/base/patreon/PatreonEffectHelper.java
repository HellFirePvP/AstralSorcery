/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.base.patreon;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.client.data.config.entry.RenderingConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PatreonEffectHelper
 * Created by HellFirePvP
 * Date: 30.08.2019 / 18:27
 */
public class PatreonEffectHelper {

    static boolean loadingFinished = false;
    static Map<UUID, List<PatreonEffect>> playerEffectMap = new HashMap<>();
    static Map<UUID, PatreonEffect> effectMap = new HashMap<>();

    @Nonnull
    public static List<PatreonEffect> getPatreonEffects(LogicalSide side, UUID playerUUID) {
        if (side.isClient() && !RenderingConfig.CONFIG.patreonEffects.get()) {
            return Collections.emptyList(); //That config is to be applied clientside
        }
        if (!loadingFinished) {
            return Collections.emptyList();
        }
        return playerEffectMap.getOrDefault(playerUUID, Collections.emptyList());
    }

    @Nullable
    public static PatreonEffect getEffect(UUID effectUUID) {
        return effectMap.get(effectUUID);
    }

    public static <T extends PlayerEntity> Map<UUID, List<PatreonEffect>> getPatreonEffects(Collection<T> players) {
        if (!loadingFinished) {
            return Maps.newHashMap();
        }
        Collection<UUID> playerUUIDs = players.stream().map(Entity::getUniqueID).collect(Collectors.toList());
        return playerEffectMap.entrySet()
                .stream()
                .filter(e -> playerUUIDs.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
