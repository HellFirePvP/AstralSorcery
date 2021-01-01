/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import hellfirepvp.astralsorcery.common.util.entity.EntityUtils;
import hellfirepvp.astralsorcery.common.util.tick.TimeoutListContainer;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkCooldownHelper
 * Created by HellFirePvP
 * Date: 25.08.2019 / 22:02
 */
public class PerkCooldownHelper {

    private static final TimeoutListContainer<UUID, ResourceLocation> perkCooldowns =
            new TimeoutListContainer<>(new PerkTimeoutHandler(LogicalSide.SERVER), TickEvent.Type.SERVER);
    private static final TimeoutListContainer<UUID, ResourceLocation> perkCooldownsClient =
            new TimeoutListContainer<>(new PerkTimeoutHandler(LogicalSide.CLIENT), TickEvent.Type.CLIENT);

    private PerkCooldownHelper() {}

    public static void attachTickListeners(Consumer<ITickHandler> registrar) {
        registrar.accept(perkCooldowns);
        registrar.accept(perkCooldownsClient);
    }

    public static void clearCache(LogicalSide side) {
        if (side.isClient()) {
            perkCooldownsClient.clear();
        } else {
            perkCooldowns.clear();
        }
    }

    public static void removeAllCooldowns(PlayerEntity player, LogicalSide side) {
        UUID playerUUID = player.getUniqueID();
        if (side.isClient()) {
            if (perkCooldownsClient.hasList(playerUUID)) {
                perkCooldownsClient.removeList(playerUUID);
            }
        } else {
            if (perkCooldowns.hasList(playerUUID)) {
                perkCooldowns.removeList(playerUUID);
            }
        }
    }

    public static void removePerkCooldowns(LogicalSide side, AbstractPerk perk) {
        if (!(perk instanceof CooldownPerk)) return;

        TimeoutListContainer<UUID, ResourceLocation> container = side.isClient() ?
                perkCooldownsClient : perkCooldowns;
        container.removeList(key -> key.equals(perk.getRegistryName()));
    }

    public static boolean isCooldownActiveForPlayer(PlayerEntity player, AbstractPerk perk) {
        if (!(perk instanceof CooldownPerk)) return false;

        TimeoutListContainer<UUID, ResourceLocation> container = player.getEntityWorld().isRemote ?
                perkCooldownsClient : perkCooldowns;
        UUID playerUUID = player.getUniqueID();
        return container.hasList(playerUUID) &&
                container.getOrCreateList(playerUUID).contains(perk.getRegistryName());
    }

    public static void setCooldownActiveForPlayer(PlayerEntity player, AbstractPerk perk, int cooldownTicks) {
        if (!(perk instanceof CooldownPerk)) return;

        TimeoutListContainer<UUID, ResourceLocation> container = player.getEntityWorld().isRemote ?
                perkCooldownsClient : perkCooldowns;
        UUID playerUUID = player.getUniqueID();
        container.getOrCreateList(playerUUID).setOrAddTimeout(cooldownTicks, perk.getRegistryName());
    }

    public static void forceSetCooldownForPlayer(PlayerEntity player, AbstractPerk perk, int cooldownTicks) {
        if (!(perk instanceof CooldownPerk)) return;

        TimeoutListContainer<UUID, ResourceLocation> container = player.getEntityWorld().isRemote ?
                perkCooldownsClient : perkCooldowns;
        UUID playerUUID = player.getUniqueID();
        if (!container.getOrCreateList(playerUUID).setTimeout(cooldownTicks, perk.getRegistryName())) {
            setCooldownActiveForPlayer(player, perk, cooldownTicks);
        }
    }

    public static int getActiveCooldownForPlayer(PlayerEntity player, AbstractPerk perk) {
        if (!(perk instanceof CooldownPerk)) return -1;

        TimeoutListContainer<UUID, ResourceLocation> container = player.getEntityWorld().isRemote ?
                perkCooldownsClient : perkCooldowns;
        UUID playerUUID = player.getUniqueID();
        if (!container.hasList(playerUUID)) {
            return -1;
        }
        return container.getOrCreateList(playerUUID).getTimeout(perk.getRegistryName());
    }

    public static class PerkTimeoutHandler implements TimeoutListContainer.ContainerTimeoutDelegate<UUID, ResourceLocation> {

        private final LogicalSide side;

        public PerkTimeoutHandler(LogicalSide side) {
            this.side = side;
        }

        @Override
        public void onContainerTimeout(UUID playerUUID, ResourceLocation key) {
            PlayerEntity player = EntityUtils.getPlayer(playerUUID, this.side);
            if (player != null) {
                PerkTree.PERK_TREE.getPerk(this.side, key).ifPresent(perk -> {
                    if (perk instanceof CooldownPerk) {
                        ((CooldownPerk) perk).onCooldownTimeout(player);
                    }
                });
            }
        }
    }
}
