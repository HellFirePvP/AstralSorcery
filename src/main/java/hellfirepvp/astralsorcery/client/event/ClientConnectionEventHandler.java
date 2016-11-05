package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.gui.GuiJournalProgression;
import hellfirepvp.astralsorcery.common.data.DataLightBlockEndpoints;
import hellfirepvp.astralsorcery.common.data.DataLightConnections;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientConnectionEventHandler
 * Created by HellFirePvP
 * Date: 02.08.2016 / 13:05
 */
public class ClientConnectionEventHandler {

    @SubscribeEvent
    public void onDc(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        AstralSorcery.log.info("[AstralSorcery] Disconnected from server. Cleaning client cache.");
        EffectHandler.cleanUp();
        Config.rebuildClientConfig();
        GuiJournalProgression.resetJournal(); //Refresh journal gui
        ResearchManager.clientProgress = new PlayerProgress();
        ((DataLightConnections) SyncDataHolder.getDataClient(SyncDataHolder.DATA_LIGHT_CONNECTIONS)).clientClean();
        ((DataLightBlockEndpoints) SyncDataHolder.getDataClient(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS)).clientClean();
    }

}
