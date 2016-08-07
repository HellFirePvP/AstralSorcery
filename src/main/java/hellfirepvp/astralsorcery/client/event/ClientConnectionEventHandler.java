package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.client.effect.EffectHandler;
import hellfirepvp.astralsorcery.common.data.DataLightConnections;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
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
        EffectHandler.cleanUp();
        ((DataLightConnections) SyncDataHolder.getDataClient(SyncDataHolder.DATA_LIGHT_CONNECTIONS)).clientClean();
    }

}
