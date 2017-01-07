/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2017
 *
 * This project is licensed under GNU GENERAL PUBLIC LICENSE Version 3.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.DataWorldSkyHandlers;
import hellfirepvp.astralsorcery.common.data.SyncDataHolder;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktWorldHandlerSyncEarly
 * Created by HellFirePvP
 * Date: 07.01.2017 / 15:25
 */
public class PktWorldHandlerSyncEarly implements Packet {

    private List<Integer> dimensionIds = new LinkedList<>();

    public PktWorldHandlerSyncEarly() {}

    public PktWorldHandlerSyncEarly(List<Integer> dimensionIds) {
        this.dimensionIds = dimensionIds;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        dimensionIds.clear();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            dimensionIds.add(buf.readInt());
        }
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeInt(dimensionIds.size());
        for (Integer dimId : dimensionIds) {
            buf.writeInt(dimId);
        }
    }

    @Override
    public void processPacket(INetHandler handler) {
        ((DataWorldSkyHandlers) SyncDataHolder.getDataClient(SyncDataHolder.DATA_SKY_HANDLERS)).updateClient(dimensionIds);
    }

}
