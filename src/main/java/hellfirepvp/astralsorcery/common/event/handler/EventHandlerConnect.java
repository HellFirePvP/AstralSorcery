/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.handler;

import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.research.ResearchSyncHelper;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerConnect
 * Created by HellFirePvP
 * Date: 24.08.2019 / 13:52
 */
public class EventHandlerConnect {

    private EventHandlerConnect() {}

    public static void attachListeners(IEventBus eventBus) {
        eventBus.addListener(EventHandlerConnect::onPlayerConnect);
    }

    private static void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
        PlayerEntity player = event.getPlayer();

        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (GeneralConfig.CONFIG.giveJournalOnJoin.get() && !progress.didReceiveTome()) {
            if (player.inventory.addItemStackToInventory(new ItemStack(ItemsAS.TOME))) {
                ResearchManager.setTomeReceived(player);
            }
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
    }

}
