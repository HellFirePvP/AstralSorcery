/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.integrations.mods.jei.util;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: JEISessionHandler
 * Created by HellFirePvP
 * Date: 22.12.2018 / 19:45
 */
public class JEISessionHandler {

    private static final JEISessionHandler INSTANCE = new JEISessionHandler();

    private boolean jeiOnServer = false;

    public static JEISessionHandler getInstance() {
        return INSTANCE;
    }

    private JEISessionHandler() {}

    public void setJeiOnServer(boolean jeiOnServer) {
        this.jeiOnServer = jeiOnServer;
    }

    public boolean isJeiOnServer() {
        return jeiOnServer;
    }

    @SubscribeEvent
    public void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (!event.isLocal() && !event.getConnectionType().equals("MODDED")) {
            jeiOnServer = false;
        }
    }

}
