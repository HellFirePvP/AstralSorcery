/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import hellfirepvp.astralsorcery.common.util.tick.TimeoutListContainer;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkCooldownHelper
 * Created by HellFirePvP
 * Date: 25.08.2019 / 22:02
 */
public class PerkCooldownHelper {

    private static TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> perkCooldowns =
            new TimeoutListContainer<>(new PerkTimeoutHandler(), TickEvent.Type.SERVER);
    private static TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> perkCooldownsClient =
            new TimeoutListContainer<>(new PerkTimeoutHandler(), TickEvent.Type.CLIENT);

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
        PlayerWrapperContainer ct = new PlayerWrapperContainer(player);
        if (side.isClient()) {
            if (perkCooldownsClient.hasList(ct)) {
                perkCooldownsClient.removeList(ct);
            }
        } else {
            if (perkCooldowns.hasList(ct)) {
                perkCooldowns.removeList(ct);
            }
        }
    }

    public static boolean isCooldownActiveForPlayer(PlayerEntity player, AbstractPerk perk) {
        if (!(perk instanceof CooldownPerk)) return false;

        TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> container = player.getEntityWorld().isRemote ?
                perkCooldownsClient : perkCooldowns;
        PlayerWrapperContainer ct = new PlayerWrapperContainer(player);
        return container.hasList(ct) &&
                container.getOrCreateList(ct).contains(perk.getRegistryName());
    }

    public static void setCooldownActiveForPlayer(PlayerEntity player, AbstractPerk perk, int cooldownTicks) {
        if (!(perk instanceof CooldownPerk)) return;

        TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> container = player.getEntityWorld().isRemote ?
                perkCooldownsClient : perkCooldowns;
        PlayerWrapperContainer ct = new PlayerWrapperContainer(player);
        container.getOrCreateList(ct).setOrAddTimeout(cooldownTicks, perk.getRegistryName());
    }

    public static void forceSetCooldownForPlayer(PlayerEntity player, AbstractPerk perk, int cooldownTicks) {
        if (!(perk instanceof CooldownPerk)) return;

        TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> container = player.getEntityWorld().isRemote ?
                perkCooldownsClient : perkCooldowns;
        PlayerWrapperContainer ct = new PlayerWrapperContainer(player);
        if (!container.getOrCreateList(ct).setTimeout(cooldownTicks, perk.getRegistryName())) {
            setCooldownActiveForPlayer(player, perk, cooldownTicks);
        }
    }

    public static int getActiveCooldownForPlayer(PlayerEntity player, AbstractPerk perk) {
        if (!(perk instanceof CooldownPerk)) return -1;

        TimeoutListContainer<PlayerWrapperContainer, ResourceLocation> container = player.getEntityWorld().isRemote ?
                perkCooldownsClient : perkCooldowns;
        PlayerWrapperContainer ct = new PlayerWrapperContainer(player);
        if (!container.hasList(ct)) {
            return -1;
        }
        return container.getOrCreateList(ct).getTimeout(perk.getRegistryName());
    }

    public static class PerkTimeoutHandler implements TimeoutListContainer.ContainerTimeoutDelegate<PlayerWrapperContainer, ResourceLocation> {

        @Override
        public void onContainerTimeout(PlayerWrapperContainer plWrapper, ResourceLocation key) {
            AbstractPerk perk = PerkTree.PERK_TREE.getPerk(key);
            if (perk instanceof CooldownPerk) {
                ((CooldownPerk) perk).onCooldownTimeout(plWrapper.player);
            }
        }
    }

    public static class PlayerWrapperContainer {

        @Nonnull
        public final PlayerEntity player;

        public PlayerWrapperContainer(@Nonnull PlayerEntity player) {
            this.player = player;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (!(obj instanceof PlayerWrapperContainer)) return false;
            return ((PlayerWrapperContainer) obj).player.getUniqueID().equals(player.getUniqueID());
        }

        @Override
        public int hashCode() {
            return player.getUniqueID().hashCode();
        }

    }
}
