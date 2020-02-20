/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.helper;

import hellfirepvp.astralsorcery.common.util.tick.TimeoutList;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.TickEvent;

import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHelperTemporaryFlight
 * Created by HellFirePvP
 * Date: 30.06.2019 / 15:36
 */
public class EventHelperTemporaryFlight {

    private static TimeoutList<PlayerEntity> ritualFlight = new TimeoutList<>(player -> {
        if (player instanceof ServerPlayerEntity && ((ServerPlayerEntity) player).interactionManager.getGameType().isSurvivalOrAdventure()) {
            player.abilities.allowFlying = false;
            player.abilities.isFlying = false;
            player.sendPlayerAbilities();
        }
    }, TickEvent.Type.SERVER);

    private EventHelperTemporaryFlight() {}

    public static void clearServer() {
        ritualFlight.clear();
    }

    public static void onDisconnect(ServerPlayerEntity player) {
        ritualFlight.remove(player);
    }

    public static void attachTickListener(Consumer<ITickHandler> registrar) {
        registrar.accept(ritualFlight);
    }

    public static boolean allowFlight(PlayerEntity entity) {
        return ritualFlight.setOrAddTimeout(20, entity);
    }

}
