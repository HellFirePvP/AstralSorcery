/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 *  All rights reserved.
 *  The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 *  For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.handler;

import hellfirepvp.astralsorcery.common.item.base.ItemBlockStorage;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.client.PktClearBlockStorageStack;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerBlockStorage
 * Created by HellFirePvP
 * Date: 23.02.2020 / 19:24
 */
public class EventHandlerBlockStorage {

    public static void attachListeners(IEventBus bus) {
        bus.addListener(EventHandlerBlockStorage::onClickAir);
        bus.addListener(EventHandlerBlockStorage::onClickBlockServer);
    }

    private static void onClickBlockServer(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack held = event.getItemStack();
        if (!event.getWorld().isRemote() && !held.isEmpty() && held.getItem() instanceof ItemBlockStorage) {
            ItemBlockStorage.clearContainerFor(event.getPlayer());
        }
    }

    //Only runs on clientside
    private static void onClickAir(PlayerInteractEvent.LeftClickEmpty event) {
        ItemStack held = event.getItemStack();
        if (!held.isEmpty() && held.getItem() instanceof ItemBlockStorage) {
            PacketChannel.CHANNEL.sendToServer(new PktClearBlockStorageStack());
        }
    }

}
