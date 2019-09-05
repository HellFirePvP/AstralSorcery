/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.handler;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHandler;
import hellfirepvp.astralsorcery.client.screen.journal.ScreenJournalProgression;
import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.effect.ConstellationEffectRegistry;
import hellfirepvp.astralsorcery.common.data.config.entry.GeneralConfig;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.data.research.ResearchSyncHelper;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataHolder;
import hellfirepvp.astralsorcery.common.event.helper.EventHelperRitualFlight;
import hellfirepvp.astralsorcery.common.event.helper.EventHelperSpawnDeny;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.PerkCooldownHelper;
import hellfirepvp.astralsorcery.common.perk.PerkEffectHelper;
import hellfirepvp.astralsorcery.common.perk.PerkTree;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import hellfirepvp.astralsorcery.common.starlight.network.StarlightTransmissionHandler;
import hellfirepvp.astralsorcery.common.util.time.TimeStopController;
import hellfirepvp.astralsorcery.common.util.world.WorldSeedCache;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerCache
 * Created by HellFirePvP
 * Date: 31.08.2019 / 11:29
 */
public class EventHandlerCache {

    private EventHandlerCache() {}

    public static void attachListeners(IEventBus eventBus) {
        eventBus.addListener(EventHandlerCache::onUnload);
        eventBus.addListener(EventHandlerCache::onClientDisconnect);
        eventBus.addListener(EventHandlerCache::onPlayerConnect);
        eventBus.addListener(EventHandlerCache::onPlayerDisconnect);
        eventBus.addListener(EventHandlerCache::onPlayerClone);
    }

    private static void onClientDisconnect(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        EffectHandler.cleanUp();
        ScreenJournalProgression.resetJournal();
        ConstellationEffectRegistry.clearClient();

        SyncDataHolder.clear(LogicalSide.CLIENT);
        PerkTree.PERK_TREE.clearCache(LogicalSide.CLIENT);
        PerkAttributeHelper.clearClient();
        PerkAttributeType.clearCache(LogicalSide.CLIENT);
        PerkCooldownHelper.clearCache(LogicalSide.CLIENT);

        WorldSeedCache.clearClient();
        AstralSorcery.log.info("Client cache cleared!");
    }

    public static void onServerStart() {

    }

    public static void onServerStop() {
        SyncDataHolder.clear(LogicalSide.SERVER);
        PerkTree.PERK_TREE.clearCache(LogicalSide.SERVER);
        PerkAttributeHelper.clearServer();
        PerkAttributeType.clearCache(LogicalSide.SERVER);
        PerkCooldownHelper.clearCache(LogicalSide.SERVER);

        StarlightTransmissionHandler.getInstance().clearServer();
        EventHelperRitualFlight.clearServer();
        EventHelperSpawnDeny.clearServer();
        ResearchHelper.saveAndClearServerCache();
    }

    private static void onUnload(WorldEvent.Unload event) {
        IWorld w = event.getWorld();

        SkyHandler.getInstance().informWorldUnload(w);
        SyncDataHolder.clearWorld(w);
        TimeStopController.onWorldUnload(w);
        StarlightTransmissionHandler.getInstance().informWorldUnload(w);
    }

    private static void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

        PlayerProgress progress = ResearchHelper.getProgress(player, LogicalSide.SERVER);
        if (GeneralConfig.CONFIG.giveJournalOnJoin.get() && !progress.didReceiveTome()) {
            if (player.inventory.addItemStackToInventory(new ItemStack(ItemsAS.TOME))) {
                ResearchManager.setTomeReceived(player);
            }
        }

        ResearchSyncHelper.pushProgressToClientUnsafe(progress, player);
        PerkEffectHelper.onPlayerConnectEvent(player);
    }

    private static void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();

        EventHelperRitualFlight.onDisconnect(player);
        PerkEffectHelper.onPlayerDisconnectEvent(player);
    }

    private static void onPlayerClone(PlayerEvent.Clone event) {
        PerkEffectHelper.onPlayerCloneEvent((ServerPlayerEntity) event.getOriginal(), (ServerPlayerEntity) event.getPlayer());
    }
}
