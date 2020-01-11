/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.handler;

import hellfirepvp.astralsorcery.common.item.base.OverrideInteractItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerInteract
 * Created by HellFirePvP
 * Date: 24.08.2019 / 15:58
 */
public class EventHandlerInteract {

    private EventHandlerInteract() {}

    public static void attachListeners(IEventBus eventBus) {
        eventBus.addListener(EventHandlerInteract::onInteract);
    }

    private static void onInteract(PlayerInteractEvent.RightClickBlock event) {
        ItemStack held = event.getItemStack();
        if (held.getItem() instanceof OverrideInteractItem) {
            OverrideInteractItem item = (OverrideInteractItem) held.getItem();
            if (item.shouldInterceptInteract(event.getSide(), event.getPlayer(), event.getHand(), event.getPos(), event.getFace()) &&
                    item.doInteract(event.getSide(), event.getPlayer(), event.getHand(), event.getPos(), event.getFace())) {
                event.setCanceled(true);
                event.setCancellationResult(ActionResultType.SUCCESS);
            }
        }
    }

}
