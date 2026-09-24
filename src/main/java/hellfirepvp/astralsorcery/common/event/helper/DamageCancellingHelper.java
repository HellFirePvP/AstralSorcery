/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.helper;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: DamageCancellingHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class DamageCancellingHelper {

    private static final Map<UUID, Set<ResourceKey<DamageType>>> invulnerableTypes = new HashMap<>();
    private static final Map<UUID, Integer> groundTicks = new HashMap<>();

    private DamageCancellingHelper() {}

    public static void preventNextDamage(Player player, ResourceKey<DamageType> type) {
        if (!(player instanceof ServerPlayer)) return;
        invulnerableTypes.computeIfAbsent(player.getUUID(), id -> new HashSet<>()).add(type);
    }

    public static void clearServer() {
        invulnerableTypes.clear();
    }

    public static void attachListeners(IEventBus bus) {
        bus.addListener(DamageCancellingHelper::onDamage);
        bus.addListener(DamageCancellingHelper::onPlayerTick);
    }

    private static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer sPlayer && sPlayer.onGround()) {
            UUID playerId = sPlayer.getUUID();
            Set<ResourceKey<DamageType>> types = invulnerableTypes.getOrDefault(playerId, Collections.emptySet());
            if (types.contains(DamageTypes.FALL)) {
                int ticks = groundTicks.getOrDefault(playerId, 0) + 1;
                if (ticks > 5) {
                    types.remove(DamageTypes.FALL);
                    if (types.isEmpty()) {
                        invulnerableTypes.remove(playerId);
                    }
                    groundTicks.remove(playerId);
                } else {
                    groundTicks.put(playerId, ticks);
                }
            } else {
                groundTicks.remove(playerId);
            }
        }
    }

    private static void onDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer sPlayer)) return;
        ResourceKey<DamageType> type = event.getSource().typeHolder().getKey();
        Set<ResourceKey<DamageType>> sources = invulnerableTypes.getOrDefault(sPlayer.getUUID(), Collections.emptySet());
        if (sources.remove(type)) {
            if (sources.isEmpty()) {
                invulnerableTypes.remove(sPlayer.getUUID());
            }
            event.setCanceled(true);
        }
    }
}
