/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.helper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHelperDamageCancelling
 * Created by HellFirePvP
 * Date: 05.01.2021 / 20:40
 */
public class EventHelperDamageCancelling {

    private static final Map<UUID, Set<DamageSource>> invulnerableTypes = new HashMap<>();

    private EventHelperDamageCancelling() {}

    public static void markInvulnerableToNextDamage(PlayerEntity player, DamageSource source) {
        if (player.getEntityWorld().isRemote()) {
            return;
        }
        invulnerableTypes.computeIfAbsent(player.getUniqueID(), uuid -> new HashSet<>()).add(source);
    }

    public static void attachListeners(IEventBus bus) {
        bus.addListener(EventHelperDamageCancelling::onLivingDamage);
        bus.addListener(EventHelperDamageCancelling::onPlayerTick);
    }

    private static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        if (event.phase == TickEvent.Phase.END && !player.getEntityWorld().isRemote()) {
            if (player.isOnGround()) {
                Set<DamageSource> sources = invulnerableTypes.getOrDefault(event.player.getUniqueID(), Collections.emptySet());
                sources.remove(DamageSource.FALL);
            }
        }
    }

    private static void onLivingDamage(LivingHurtEvent event) {
        if (!(event.getEntityLiving() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) event.getEntityLiving();
        Set<DamageSource> sources = invulnerableTypes.getOrDefault(player.getUniqueID(), Collections.emptySet());
        if (sources.remove(event.getSource())) {
            if (sources.isEmpty()) {
                invulnerableTypes.remove(player.getUniqueID());
            }

            event.setCanceled(true);
        }
    }
}
