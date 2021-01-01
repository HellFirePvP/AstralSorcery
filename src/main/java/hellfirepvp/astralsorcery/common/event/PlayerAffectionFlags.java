/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import hellfirepvp.astralsorcery.common.util.tick.TimeoutList;
import hellfirepvp.astralsorcery.common.util.tick.TimeoutListContainer;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.TickEvent;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PlayerAffectionFlags
 * Created by HellFirePvP
 * Date: 31.12.2020 / 13:04
 */
public class PlayerAffectionFlags {

    private static final int DEFAULT_TICK_TIMEOUT = 10;
    private static final TimeoutListContainer<UUID, AffectionFlag> affectMap = new TimeoutListContainer<>(new TimeoutListContainer.ForwardingTimeoutDelegate<>(), TickEvent.Type.SERVER);

    private PlayerAffectionFlags() {}

    public static void attachTickListeners(Consumer<ITickHandler> registrar) {
        registrar.accept(affectMap);
    }

    public static void clearServerCache() {
        affectMap.clear();
    }

    public static void markPlayerAffected(PlayerEntity player, AffectionFlag flag) {
        affectMap.getOrCreateList(player.getUniqueID()).setOrAddTimeout(DEFAULT_TICK_TIMEOUT, flag);
    }

    public static boolean isPlayerAffected(PlayerEntity player, AffectionFlag flag) {
        UUID playerUUID = player.getUniqueID();
        return affectMap.hasList(playerUUID) && affectMap.getOrCreateList(playerUUID).contains(flag);
    }

    public static abstract class AffectionFlag implements TimeoutList.TimeoutDelegate<UUID> {

        private final ResourceLocation key;

        public AffectionFlag(ResourceLocation key) {
            this.key = key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AffectionFlag that = (AffectionFlag) o;
            return Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key);
        }
    }

    public static class NoOpAffectionFlag extends AffectionFlag {

        public NoOpAffectionFlag(ResourceLocation key) {
            super(key);
        }

        @Override
        public void onTimeout(UUID object) {}
    }
}
