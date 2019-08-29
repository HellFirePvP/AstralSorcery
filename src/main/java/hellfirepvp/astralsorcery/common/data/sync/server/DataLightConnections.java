/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.server;

import hellfirepvp.astralsorcery.common.data.sync.base.AbstractData;
import hellfirepvp.astralsorcery.common.data.sync.base.AbstractDataProvider;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientDataReader;
import hellfirepvp.astralsorcery.common.data.sync.client.ClientLightConnections;
import hellfirepvp.astralsorcery.common.starlight.network.TransmissionChain;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataStarlightConnections
 * Created by HellFirePvP
 * Date: 05.08.2016 / 20:14
 */
public class DataLightConnections extends AbstractData {

    private Map<Integer, Map<BlockPos, Set<BlockPos>>> serverPosBuffer = new HashMap<>();

    //Boolean flag: true=addition, false=removal
    private Map<Integer, LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>>> serverChangeBuffer = new HashMap<>();
    private Set<Integer> dimensionClearBuffer = new HashSet<>();

    private DataLightConnections(ResourceLocation key) {
        super(key);
    }

    public void updateNewConnectionsThreaded(int dimensionId, List<TransmissionChain.LightConnection> newlyAddedConnections) {
        Map<BlockPos, Set<BlockPos>> posBufferDim = serverPosBuffer.computeIfAbsent(dimensionId, k -> new HashMap<>());
        for (TransmissionChain.LightConnection c : newlyAddedConnections) {
            BlockPos start = c.getStart();
            Set<BlockPos> endpoints = posBufferDim.computeIfAbsent(start, k -> new HashSet<>());
            endpoints.add(c.getEnd());
        }
        notifyConnectionAdd(dimensionId, newlyAddedConnections);
        if (newlyAddedConnections.size() > 0) {
            markDirty();
        }
    }

    public void removeOldConnectionsThreaded(int dimensionId, List<TransmissionChain.LightConnection> invalidConnections) {
        Map<BlockPos, Set<BlockPos>> posBufferDim = serverPosBuffer.get(dimensionId);
        if (posBufferDim != null) {
            for (TransmissionChain.LightConnection c : invalidConnections) {
                BlockPos start = c.getStart();
                Set<BlockPos> ends = posBufferDim.get(start);
                if (ends == null) {
                    continue;
                }
                ends.remove(c.getEnd());
                if(ends.isEmpty()) {
                    posBufferDim.remove(start);
                }
            }
        }
        notifyConnectionRemoval(dimensionId, invalidConnections);
        if (invalidConnections.size() > 0) {
            markDirty();
        }
    }

    @Override
    public void clear(int dimId) {
        if (this.serverPosBuffer.remove(dimId) != null) {
            this.dimensionClearBuffer.add(dimId);
            markDirty();
        }
    }

    private void notifyConnectionAdd(int dimid, List<TransmissionChain.LightConnection> added) {
        LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>> ch = serverChangeBuffer.computeIfAbsent(dimid, k -> new LinkedList<>());
        for (TransmissionChain.LightConnection l : added) {
            ch.add(new Tuple<>(l, true));
        }
        this.dimensionClearBuffer.remove(dimid);
    }

    private void notifyConnectionRemoval(int dimid, List<TransmissionChain.LightConnection> removal) {
        LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>> ch = serverChangeBuffer.computeIfAbsent(dimid, k -> new LinkedList<>());
        for (TransmissionChain.LightConnection l : removal) {
            ch.add(new Tuple<>(l, false));
        }
    }

    @Override
    public void writeAllDataToPacket(CompoundNBT compound) {
        for (int dimId : serverPosBuffer.keySet()) {
            Map<BlockPos, Set<BlockPos>> dat = serverPosBuffer.get(dimId);
            ListNBT dataList = new ListNBT();
            for (BlockPos start : dat.keySet()) {
                Set<BlockPos> endPositions = dat.get(start);
                if (endPositions == null) {
                    continue;
                }

                for (BlockPos end : endPositions) {
                    CompoundNBT cmp = new CompoundNBT();
                    cmp.putLong("start", start.toLong());
                    cmp.putLong("end",   end.toLong());
                    dataList.add(cmp);
                }
            }

            compound.put(String.valueOf(dimId), dataList);
        }
    }

    @Override
    public void writeDiffDataToPacket(CompoundNBT compound) {
        ListNBT clearList = new ListNBT();
        for (int dimId : this.dimensionClearBuffer) {
            clearList.add(new IntNBT(dimId));
        }
        compound.put("clear", clearList);

        for (int dimId : serverChangeBuffer.keySet()) {
            if (this.dimensionClearBuffer.contains(dimId)) {
                continue;
            }

            LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>> changes = serverChangeBuffer.get(dimId);
            if (!changes.isEmpty()) {
                ListNBT list = new ListNBT();
                for (Tuple<TransmissionChain.LightConnection, Boolean> tuple : changes) {
                    CompoundNBT connection = new CompoundNBT();
                    connection.putLong("start", tuple.getA().getStart().toLong());
                    connection.putLong("end",   tuple.getA().getEnd().toLong());
                    connection.putBoolean("connect", tuple.getB());
                    list.add(connection);
                }
                compound.put(String.valueOf(dimId), list);
            }
        }

        this.dimensionClearBuffer.clear();
        this.serverChangeBuffer.clear();
    }

    public static class Provider extends AbstractDataProvider<DataLightConnections, ClientLightConnections> {

        public Provider(ResourceLocation key) {
            super(key);
        }

        @Override
        public DataLightConnections provideServerData() {
            return new DataLightConnections(getKey());
        }

        @Override
        public ClientLightConnections provideClientData() {
            return new ClientLightConnections();
        }

        @Override
        public ClientDataReader<ClientLightConnections> createReader() {
            return new ClientLightConnections.Reader();
        }
    }


}
