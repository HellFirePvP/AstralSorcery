/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.gui.GuiJournalProgression;
import hellfirepvp.astralsorcery.client.render.tile.TESRTranslucentBlock;
import hellfirepvp.astralsorcery.client.util.ClientScreenshotCache;
import hellfirepvp.astralsorcery.client.util.camera.ClientCameraManager;
import hellfirepvp.astralsorcery.common.base.CelestialGatewaySystem;
import hellfirepvp.astralsorcery.common.constellation.charge.PlayerChargeHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.perk.ConstellationPerkLevelManager;
import hellfirepvp.astralsorcery.common.crafting.helper.CraftingAccessManager;
import hellfirepvp.astralsorcery.common.data.DataLightBlockEndpoints;
import hellfirepvp.astralsorcery.common.data.DataLightConnections;
import hellfirepvp.astralsorcery.common.data.DataWorldSkyHandlers;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.HashMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientConnectionEventHandler
 * Created by HellFirePvP
 * Date: 02.08.2016 / 13:05
 */
public class ClientConnectionEventHandler {

    //Used to cleanup stuff on clientside to make the client functional to switch servers at any time.
    @SubscribeEvent
    public void onDc(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        AstralSorcery.log.info("[AstralSorcery] Disconnected from server. Cleaning client cache.");
        EffectHandler.cleanUp();
        ClientCameraManager.getInstance().removeAllAndCleanup();
        Config.rebuildClientConfig();
        ConstellationSkyHandler.getInstance().clientClearCache();
        GuiJournalProgression.resetJournal(); //Refresh journal gui
        ResearchManager.clientProgress = new PlayerProgress();
        AstralSorcery.proxy.scheduleClientside(ClientScreenshotCache::cleanUp);
        ConstellationPerkLevelManager.levelsRequired = ConstellationPerkLevelManager.levelsRequiredClientCache;
        ClientRenderEventHandler.resetPermChargeReveal();
        ClientRenderEventHandler.resetTempChargeReveal();
        AstralSorcery.proxy.scheduleClientside(TESRTranslucentBlock::cleanUp);
        PlayerChargeHandler.instance.setClientCharge(0F);
        CraftingAccessManager.clearModifications();
        CelestialGatewaySystem.instance.updateClientCache(new HashMap<>());
        ((DataLightConnections) SyncDataHolder.getDataClient(SyncDataHolder.DATA_LIGHT_CONNECTIONS)).clientClean();
        ((DataLightBlockEndpoints) SyncDataHolder.getDataClient(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS)).clientClean();
        ((DataWorldSkyHandlers) SyncDataHolder.getDataClient(SyncDataHolder.DATA_SKY_HANDLERS)).clientClean();
    }

    @SubscribeEvent
    public void onJoin(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        AstralSorcery.proxy.scheduleClientside(() -> {
            NetworkManager nm = event.getManager();
            String addr = nm.getRemoteAddress().toString();
            if (nm.isLocalChannel()) {
                IntegratedServer is = Minecraft.getMinecraft().getIntegratedServer();
                if(is != null) {
                    addr = is.getWorldName();
                }
            }
            ClientScreenshotCache.loadAndInitScreenshotsFor(addr);
        });
    }

}
