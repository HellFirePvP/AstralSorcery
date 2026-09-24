/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.tick;

import hellfirepvp.astralsorcery.common.perk.data.PerkTree;
import hellfirepvp.astralsorcery.common.perk.tree.AbstractPerk;
import hellfirepvp.astralsorcery.common.util.EntityUtil;
import hellfirepvp.astralsorcery.common.util.data.SidedReference;
import hellfirepvp.astralsorcery.common.util.tick.TimeoutListContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.LogicalSide;

import java.util.UUID;
import java.util.function.Consumer;

import static hellfirepvp.astralsorcery.common.util.SidedHelper.getSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkCooldownHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkCooldownHelper {

    private static final SidedReference<TimeoutListContainer<UUID, ResourceLocation>> perkCooldowns = new SidedReference<>() {
        {
            setData(LogicalSide.CLIENT, new TimeoutListContainer<>(new PerkTimeoutHandler(LogicalSide.CLIENT)));
            setData(LogicalSide.SERVER, new TimeoutListContainer<>(new PerkTimeoutHandler(LogicalSide.SERVER)));
        }
    };

    public static void attachEventListeners(IEventBus bus) {
        perkCooldowns.getData(LogicalSide.CLIENT).ifPresent(ct -> bus.addListener(ct::onClientTick));
        perkCooldowns.getData(LogicalSide.SERVER).ifPresent(st -> bus.addListener(st::onServerTick));
    }

    public static void clearCache(LogicalSide side) {
        perkCooldowns.getData(side).ifPresent(TimeoutListContainer::clear);
    }

    public static void removeAllCooldowns(Player player, LogicalSide side) {
        UUID playerUUID = player.getUUID();
        perkCooldowns.getData(side).ifPresent(ct -> {
            if (ct.hasList(playerUUID)) {
                ct.removeList(playerUUID);
            }
        });
    }

    public static void removePerkCooldowns(LogicalSide side, AbstractPerk<?> perk) {
        perkCooldowns.getData(side).ifPresent(ct -> {
            ct.removeAnyListEntry(key -> key.equals(perk.getKey()));
        });
    }

    public static boolean isCooldownActiveForPlayer(Player player, AbstractPerk<?> perk) {
        if (!(perk instanceof CooldownPerk)) return false;
        UUID playerUUID = player.getUUID();

        return perkCooldowns.getData(getSide(player)).map(ct -> {
            return ct.hasList(playerUUID) &&
                    ct.getOrCreateList(playerUUID).contains(perk.getKey());
        }).orElse(false);
    }

    public static void setCooldownActiveForPlayer(Player player, AbstractPerk<?> perk, int cooldownTicks) {
        if (!(perk instanceof CooldownPerk)) return;
        UUID playerUUID = player.getUUID();

        perkCooldowns.getData(getSide(player)).ifPresent(ct -> {
            ct.getOrCreateList(playerUUID).setOrAddTimeout(cooldownTicks, perk.getKey());
        });
    }

    public static void forceSetCooldownForPlayer(Player player, AbstractPerk<?> perk, int cooldownTicks) {
        if (!(perk instanceof CooldownPerk)) return;
        UUID playerUUID = player.getUUID();

        perkCooldowns.getData(getSide(player)).ifPresent(ct -> {
            if (!ct.getOrCreateList(playerUUID).setTimeout(cooldownTicks, perk.getKey())) {
                setCooldownActiveForPlayer(player, perk, cooldownTicks);
            }
        });
    }

    public static int getActiveCooldownForPlayer(Player player, AbstractPerk<?> perk) {
        if (!(perk instanceof CooldownPerk)) return -1;
        UUID playerUUID = player.getUUID();

        return perkCooldowns.getData(getSide(player)).map(ct -> {
            if (!ct.hasList(playerUUID)) {
                return -1;
            }
            return ct.getOrCreateList(playerUUID).getTimeout(perk.getKey());
        }).orElse(-1);
    }

    public static class PerkTimeoutHandler implements TimeoutListContainer.ContainerTimeoutDelegate<UUID, ResourceLocation> {

        private final LogicalSide side;

        public PerkTimeoutHandler(LogicalSide side) {
            this.side = side;
        }

        @Override
        public void onContainerTimeout(UUID playerUUID, ResourceLocation key) {
            EntityUtil.getPlayer(playerUUID, this.side).ifPresent(player -> {
                PerkTree.getInstance().getPerk(this.side, key).ifPresent(perk -> {
                    if (perk instanceof CooldownPerk cdPerk) {
                        cdPerk.onCooldownTimeout(player);
                    }
                });
            });
        }
    }
}
