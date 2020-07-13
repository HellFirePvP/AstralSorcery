/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
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
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataStarlightConnections
 * Created by HellFirePvP
 * Date: 05.08.2016 / 20:14
 */
public class DataLightConnections extends AbstractData {

    private Map<DimensionType, Map<BlockPos, Set<BlockPos>>> serverPosBuffer = new HashMap<>();

    //Boolean flag: true=addition, false=removal
    private Map<DimensionType, LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>>> serverChangeBuffer = new HashMap<>();
    private Set<DimensionType> dimensionClearBuffer = new HashSet<>();

    private DataLightConnections(ResourceLocation key) {
        super(key);
    }

    public void updateNewConnectionsThreaded(DimensionType dimType, List<TransmissionChain.LightConnection> newlyAddedConnections) {
        Map<BlockPos, Set<BlockPos>> posBufferDim = serverPosBuffer.computeIfAbsent(dimType, k -> new HashMap<>());
        for (TransmissionChain.LightConnection c : newlyAddedConnections) {
            BlockPos start = c.getStart();
            Set<BlockPos> endpoints = posBufferDim.computeIfAbsent(start, k -> new HashSet<>());
            endpoints.add(c.getEnd());
        }
        notifyConnectionAdd(dimType, newlyAddedConnections);
        if (newlyAddedConnections.size() > 0) {
            markDirty();
        }
    }

    public void removeOldConnectionsThreaded(DimensionType dimType, List<TransmissionChain.LightConnection> invalidConnections) {
        Map<BlockPos, Set<BlockPos>> posBufferDim = serverPosBuffer.get(dimType);
        if (posBufferDim != null) {
            for (TransmissionChain.LightConnection c : invalidConnections) {
                BlockPos start = c.getStart();
                Set<BlockPos> ends = posBufferDim.get(start);
                if (ends == null) {
                    continue;
                }
                ends.remove(c.getEnd());
                if (ends.isEmpty()) {
                    posBufferDim.remove(start);
                }
            }
        }
        notifyConnectionRemoval(dimType, invalidConnections);
        if (invalidConnections.size() > 0) {
            markDirty();
        }
    }

    @Override
    public void clear(DimensionType dimType) {
        if (this.serverPosBuffer.remove(dimType) != null) {
            this.dimensionClearBuffer.add(dimType);
            markDirty();
        }
    }

    @Override
    public void clearServer() {
        this.dimensionClearBuffer.clear();
        this.serverChangeBuffer.clear();
        this.serverPosBuffer.clear();
    }

    private void notifyConnectionAdd(DimensionType dimType, List<TransmissionChain.LightConnection> added) {
        LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>> ch = serverChangeBuffer.computeIfAbsent(dimType, k -> new LinkedList<>());
        for (TransmissionChain.LightConnection l : added) {
            ch.add(new Tuple<>(l, true));
        }
        this.dimensionClearBuffer.remove(dimType);
    }

    private void notifyConnectionRemoval(DimensionType dimType, List<TransmissionChain.LightConnection> removal) {
        LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>> ch = serverChangeBuffer.computeIfAbsent(dimType, k -> new LinkedList<>());
        for (TransmissionChain.LightConnection l : removal) {
            ch.add(new Tuple<>(l, false));
        }
    }

    @Override
    public void writeAllDataToPacket(CompoundNBT compound) {
        for (DimensionType dimType : serverPosBuffer.keySet()) {
            Map<BlockPos, Set<BlockPos>> dat = serverPosBuffer.get(dimType);
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

            compound.put(dimType.getRegistryName().toString(), dataList);
        }
    }

    @Override
    public void writeDiffDataToPacket(CompoundNBT compound) {
        ListNBT clearList = new ListNBT();
        for (DimensionType dimType : this.dimensionClearBuffer) {
            clearList.add(StringNBT.valueOf(dimType.getRegistryName().toString()));
        }
        compound.put("clear", clearList);

        for (DimensionType dimType : serverChangeBuffer.keySet()) {
            if (this.dimensionClearBuffer.contains(dimType)) {
                continue;
            }

            LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>> changes = serverChangeBuffer.get(dimType);
            if (!changes.isEmpty()) {
                ListNBT list = new ListNBT();
                for (Tuple<TransmissionChain.LightConnection, Boolean> tuple : changes) {
                    CompoundNBT connection = new CompoundNBT();
                    connection.putLong("start", tuple.getA().getStart().toLong());
                    connection.putLong("end",   tuple.getA().getEnd().toLong());
                    connection.putBoolean("connect", tuple.getB());
                    list.add(connection);
                }
                compound.put(dimType.getRegistryName().toString(), list);
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
