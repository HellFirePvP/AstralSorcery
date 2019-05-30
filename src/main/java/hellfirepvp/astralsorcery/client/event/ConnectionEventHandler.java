/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.event;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.client.ClientProxy;
import hellfirepvp.astralsorcery.client.effect.handler.EffectHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.network.NetworkEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConnectionEventHandler
 * Created by HellFirePvP
 * Date: 30.05.2019 / 14:04
 */
public class ConnectionEventHandler {

    private static ConnectionEventHandler INSTANCE = new ConnectionEventHandler();

    private ConnectionEventHandler() {}

    public static ConnectionEventHandler getInstance() {
        return INSTANCE;
    }

    public void attachEventListeners(IEventBus bus) {

    }

    //TODO on disconnect.
    public void onDc() {
        AstralSorcery.log.info("Cleaning client cache...");
        EffectHandler.cleanUp();
        ClientProxy.connected = false;
        AstralSorcery.log.info("Cleared cached client data! Disconnected from server.");
    }

}
