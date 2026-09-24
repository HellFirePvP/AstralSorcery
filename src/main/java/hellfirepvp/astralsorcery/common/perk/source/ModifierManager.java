/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source;

import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ModifierManager
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ModifierManager {

    private static final ModifierManager INSTANCE = new ModifierManager();

    private static final Map<UUID, Set<ModifierSource>> modifierCache = new HashMap<>();
    private static final Map<UUID, Set<ModifierSource>> modifierCacheClient = new HashMap<>();

    private ModifierManager() {}

    public static ModifierManager getInstance() {
        return INSTANCE;
    }

    public void attachEventListeners(IEventBus bus) {
        bus.addListener(this::onPlayerTick);
        bus.addListener(this::onDisconnect);
    }

    private void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer sPlayer) {
            for (ModifierSourceProvider<?> sourceProvider : RegistriesAS.REGISTRY_PERK_MODIFIER_SOURCES) {
                sourceProvider.update(sPlayer);
            }
        }
    }

    @Nonnull
    private static Set<ModifierSource> getModifiers(Player player, LogicalSide side) {
        if (side.isClient()) {
            return modifierCacheClient.computeIfAbsent(player.getUUID(), uuid -> new HashSet<>());
        } else {
            return modifierCache.computeIfAbsent(player.getUUID(), uuid -> new HashSet<>());
        }
    }

    @Nonnull
    public static Set<ModifierSource> getAppliedModifiers(Player player, LogicalSide side) {
        return new HashSet<>(getModifiers(player, side));
    }

    public static void addModifier(Player player, LogicalSide side, ModifierSource source) {
        Set<ModifierSource> modifiers = getModifiers(player, side);
        if (!modifiers.contains(source) && modifiers.add(source)) {
            source.onApply(player, side);
        }
    }

    public static void removeModifier(Player player, LogicalSide side, ModifierSource source) {
        Set<ModifierSource> modifiers = getModifiers(player, side);
        if (modifiers.remove(source)) {
            source.onRemove(player, side);
        }
    }

    public static boolean isModifierApplied(Player player, LogicalSide side, ModifierSource source) {
        return getModifiers(player, side).contains(source);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clearClientCache() {
        modifierCacheClient.clear();
    }

    private void onDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer sPlayer) {
            for (ModifierSourceProvider<?> sourceProvider : RegistriesAS.REGISTRY_PERK_MODIFIER_SOURCES) {
                sourceProvider.removeModifiers(sPlayer);
            }
        }
    }
}
