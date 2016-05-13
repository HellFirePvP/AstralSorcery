package hellfirepvp.astralsorcery.common.data;

import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.server.PktSyncData;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SyncDataHolder
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:11
 */
public class SyncDataHolder {

    public static final String DATA_CONSTELLATIONS = "AstralConstellations";

    private static Map<String, AbstractData> serverData = new HashMap<>();
    private static Map<String, AbstractData> clientData = new HashMap<>();

    private static List<String> dirtyData = new ArrayList<String>();
    private static byte providerCounter = 0;

    public static void register(AbstractData.AbstractDataProvider<? extends AbstractData> provider) {
        AbstractData.Registry.register(provider);
        AbstractData ad = provider.provideNewInstance();
        ad.setProviderId(provider.getProviderId());
        serverData.put(provider.getKey(), ad);
        ad = provider.provideNewInstance();
        ad.setProviderId(provider.getProviderId());
        clientData.put(provider.getKey(), ad);
    }

    public static byte allocateNewId() {
        byte pId = providerCounter;
        providerCounter++;
        return pId;
    }

    public static <T extends AbstractData> T getDataServer(String key) {
        return (T) serverData.get(key);
    }

    public static <T extends AbstractData> T getDataClient(String key) {
        return (T) clientData.get(key);
    }

    public static void markForUpdate(String key) {
        if (!dirtyData.contains(key)) {
            dirtyData.add(key);
        }
    }

    public static void syncAllDataTo(EntityPlayer player) {
        PktSyncData dataSync = new PktSyncData(serverData, true);
        PacketChannel.CHANNEL.sendTo(dataSync, (net.minecraft.entity.player.EntityPlayerMP) player);
    }

    public static void receiveServerPacket(Map<String, AbstractData> data) {
        for (String key : data.keySet()) {
            AbstractData dat = clientData.get(key);
            if (dat != null) {
                dat.handleIncomingData(data.get(key));
            }
        }
    }

    public static void doNecessaryUpdates() {
        if (dirtyData.isEmpty()) return;
        Map<String, AbstractData> pktData = new HashMap<String, AbstractData>();
        for (String s : dirtyData) {
            AbstractData d = getDataServer(s);
            if (d.needsUpdate()) {
                pktData.put(s, d);
            }
        }
        dirtyData.clear();
        PktSyncData dataSync = new PktSyncData(pktData, false);
        PacketChannel.CHANNEL.sendToAll(dataSync);
    }

    public static void initialize() {
        register(new DataActiveCelestials.Provider(DATA_CONSTELLATIONS));
    }

}
