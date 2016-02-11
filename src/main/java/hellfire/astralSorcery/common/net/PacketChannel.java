package hellfire.astralSorcery.common.net;

import hellfire.astralSorcery.common.lib.LibConstants;
import hellfire.astralSorcery.common.net.packet.PktSyncConfig;
import hellfire.astralSorcery.common.net.packet.PktSyncData;
import hellfire.astralSorcery.common.net.packet.PktSyncKnowledge;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * <p/>
 * Created by HellFirePvP @ 24.01.2016 20:47
 */
public class PacketChannel {

    public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(LibConstants.NAME);

    public static void init() {
        int id = 0;

        //Synchronizing data
        CHANNEL.registerMessage(PktSyncConfig.class, PktSyncConfig.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktSyncKnowledge.class, PktSyncKnowledge.class, id++, Side.CLIENT);
        CHANNEL.registerMessage(PktSyncData.class, PktSyncData.class, id++, Side.CLIENT);
    }

}
