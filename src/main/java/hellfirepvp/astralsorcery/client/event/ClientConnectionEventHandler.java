/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.client.gui.GuiJournalProgression;
import hellfirepvp.astralsorcery.client.render.tile.TESRTranslucentBlock;
import hellfirepvp.astralsorcery.client.util.ClientScreenshotCache;
import hellfirepvp.astralsorcery.client.util.UISextantCache;
import hellfirepvp.astralsorcery.client.util.camera.ClientCameraManager;
import hellfirepvp.astralsorcery.common.auxiliary.CelestialGatewaySystem;
import hellfirepvp.astralsorcery.common.constellation.charge.PlayerChargeHandler;
import hellfirepvp.astralsorcery.common.constellation.distribution.ConstellationSkyHandler;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkEffectHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.type.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTree;
import hellfirepvp.astralsorcery.common.data.*;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.NetworkManager;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;

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
        AstralSorcery.log.info("[AstralSorcery] Cleaning client cache...");
        EffectHandler.cleanUp();
        ClientCameraManager.getInstance().removeAllAndCleanup();
        ConstellationSkyHandler.getInstance().clientClearCache();
        GuiJournalProgression.resetJournal(); //Refresh journal gui
        ResearchManager.clientProgress = new PlayerProgress();
        ResearchManager.clientInitialized = false;
        AstralSorcery.proxy.scheduleClientside(ClientScreenshotCache::cleanUp);
        ClientRenderEventHandler.resetPermChargeReveal();
        ClientRenderEventHandler.resetTempChargeReveal();
        AstralSorcery.proxy.scheduleClientside(TESRTranslucentBlock::cleanUp);
        PlayerChargeHandler.INSTANCE.setClientCharge(0F);
        PerkEffectHelper.perkCooldownsClient.clear();
        CelestialGatewaySystem.instance.updateClientCache(new HashMap<>());
        AttributeTypeRegistry.getTypes().forEach(t -> t.clear(Side.CLIENT));
        PerkTree.PERK_TREE.clearCache(Side.CLIENT);
        UISextantCache.INSTANCE.clearClient();
        ((DataLightConnections) SyncDataHolder.getDataClient(SyncDataHolder.DATA_LIGHT_CONNECTIONS)).clientClean();
        ((DataLightBlockEndpoints) SyncDataHolder.getDataClient(SyncDataHolder.DATA_LIGHT_BLOCK_ENDPOINTS)).clientClean();
        ((DataTimeFreezeEffects) SyncDataHolder.getDataClient(SyncDataHolder.DATA_TIME_FREEZE_EFFECTS)).clientClean();
        ((DataPatreonFlares) SyncDataHolder.getDataClient(SyncDataHolder.DATA_PATREON_FLARES)).cleanUp(Side.CLIENT);
        ClientProxy.connected = false;
        AstralSorcery.log.info("[AstralSorcery] Cleared cached client data! Disconnected from server.");
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
            } else {
                int id = addr.indexOf('\\');
                if(id != -1) {
                    addr = addr.substring(0, MathHelper.clamp(id, 1, addr.length()));
                }
            }
            addr = sanitizeFileName(addr);
            ClientScreenshotCache.loadAndInitScreenshotsFor(addr);
        });
    }

    private String sanitizeFileName(String addr) {
        addr = addr.trim();
        addr = addr.replace(' ', '_');
        addr = addr.toLowerCase();
        for (char c0 : ChatAllowedCharacters.ILLEGAL_FILE_CHARACTERS) {
            addr = addr.replace(c0, '_');
        }
        addr = addr.replaceAll("[^a-zA-Z0-9\\.\\-]", "_"); //Anything else that falls through
        return addr;
    }

}
