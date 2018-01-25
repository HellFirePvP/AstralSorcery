/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data;

import hellfirepvp.astralsorcery.common.starlight.network.TransmissionChain;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataStarlightConnections
 * Created by HellFirePvP
 * Date: 05.08.2016 / 20:14
 */
public class DataLightConnections extends AbstractData {

    private final Object lock = new Object();

    public boolean clientReceivingData = false;
    private Map<Integer, Map<BlockPos, List<BlockPos>>> clientPosBuffer = new ConcurrentHashMap<>();
    private Map<Integer, Map<BlockPos, List<BlockPos>>> serverPosBuffer = new HashMap<>();

    //Boolean flag: true=addition, false=removal
    private Map<Integer, LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>>> serverChangeBuffer = new HashMap<>();

    private NBTTagCompound clientReadBuffer = new NBTTagCompound();

    public void updateNewConnectionsThreaded(int dimensionId, List<TransmissionChain.LightConnection> newlyAddedConnections) {
        Map<BlockPos, List<BlockPos>> posBufferDim = serverPosBuffer.get(dimensionId);
        if(posBufferDim == null) {
            posBufferDim = new HashMap<>();
            serverPosBuffer.put(dimensionId, posBufferDim);
        }
        for (TransmissionChain.LightConnection c : newlyAddedConnections) {
            BlockPos start = c.getStart();
            BlockPos end = c.getEnd();
            List<BlockPos> endpoints = posBufferDim.get(start);
            if(endpoints == null) {
                endpoints = new LinkedList<>();
                posBufferDim.put(start, endpoints);
            }
            if(!endpoints.contains(end)) endpoints.add(end);
        }
        notifyConnectionAdd(dimensionId, newlyAddedConnections);
        if(newlyAddedConnections.size() > 0) {
            markDirty();
        }
    }

    public void removeOldConnectionsThreaded(int dimensionId, List<TransmissionChain.LightConnection> invalidConnections) {
        Map<BlockPos, List<BlockPos>> posBufferDim = serverPosBuffer.get(dimensionId);
        if(posBufferDim != null) {
            for (TransmissionChain.LightConnection c : invalidConnections) {
                BlockPos start = c.getStart();
                List<BlockPos> ends = posBufferDim.get(start);
                if(ends == null) continue;
                if(ends.contains(c.getEnd())) {
                    ends.remove(c.getEnd());
                }
                if(ends.isEmpty()) posBufferDim.remove(start);
            }
        }
        notifyConnectionRemoval(dimensionId, invalidConnections);
        if(invalidConnections.size() > 0) {
            markDirty();
        }
    }

    public void clearDimensionPositions(int dimId) {
        if(serverPosBuffer.remove(dimId) != null) {
            setDimClearFlag(dimId);
            markDirty();
        }
    }

    private void setDimClearFlag(int dim) {
        synchronized (lock) {
            LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>> ch = serverChangeBuffer.get(dim);
            if(ch == null) {
                ch = new LinkedList<>();
                serverChangeBuffer.put(dim, ch);
            }
            ch.clear();
            ch.add(new Tuple<>(null, false)); //null, false -> clear
        }
    }

    private void notifyConnectionAdd(int dimid, List<TransmissionChain.LightConnection> added) {
        synchronized (lock) {
            LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>> ch = serverChangeBuffer.get(dimid);
            if(ch == null) {
                ch = new LinkedList<>();
                serverChangeBuffer.put(dimid, ch);
            }
            for (TransmissionChain.LightConnection l : added) {
                ch.add(new Tuple<>(l, true));
            }
        }
    }

    private void notifyConnectionRemoval(int dimid, List<TransmissionChain.LightConnection> removal) {
        synchronized (lock) {
            LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>> ch = serverChangeBuffer.get(dimid);
            if (ch == null) {
                ch = new LinkedList<>();
                serverChangeBuffer.put(dimid, ch);
            }
            for (TransmissionChain.LightConnection l : removal) {
                ch.add(new Tuple<>(l, false));
            }
        }
    }

    @Nullable
    public Map<BlockPos, List<BlockPos>> getClientConnections(int dimId) {
        return clientPosBuffer.get(dimId);
    }

    public void clientClean() {
        clientPosBuffer.clear();
    }

    @Override
    public void writeAllDataToPacket(NBTTagCompound compound) {
        for (int dimId : serverPosBuffer.keySet()) {
            Map<BlockPos, List<BlockPos>> dat = serverPosBuffer.get(dimId);
            NBTTagList dataList = new NBTTagList();
            for (BlockPos pos : dat.keySet()) {
                List<BlockPos> connections = dat.get(pos);
                if(connections == null) continue;
                for (BlockPos end : connections) {
                    NBTTagCompound cmp = new NBTTagCompound();
                    cmp.setLong("sta", pos.toLong());
                    cmp.setLong("end", end.toLong());
                    cmp.setBoolean("s", true);
                    dataList.appendTag(cmp);
                }
            }

            compound.setTag("" + dimId, dataList);
        }
    }

    @Override
    public void writeToPacket(NBTTagCompound compound) {
        synchronized (lock) {
            for (int dimId : serverChangeBuffer.keySet()) {
                LinkedList<Tuple<TransmissionChain.LightConnection, Boolean>> changes = serverChangeBuffer.get(dimId);
                if(!changes.isEmpty()) {
                    NBTTagList list = new NBTTagList();
                    for (Tuple<TransmissionChain.LightConnection, Boolean> tpl : changes) {
                        if(tpl.key == null) {
                            list = new NBTTagList();
                            NBTTagCompound cm = new NBTTagCompound();
                            cm.setBoolean("clear", true);
                            list.appendTag(cm);
                            break;
                        }

                        NBTTagCompound cmp = new NBTTagCompound();
                        cmp.setLong("sta", tpl.key.getStart().toLong());
                        cmp.setLong("end", tpl.key.getEnd().toLong());
                        cmp.setBoolean("s", tpl.value);
                        list.appendTag(cmp);
                    }

                    compound.setTag("" + dimId, list);
                }
            }
            serverChangeBuffer.clear();
        }
    }

    @Override
    public void readRawFromPacket(NBTTagCompound compound) {
        this.clientReadBuffer = compound;
    }

    @Override
    public void handleIncomingData(AbstractData serverData) {
        if(!(serverData instanceof DataLightConnections)) return;

        clientReceivingData = true;
        try {
            for (String dimStr : ((DataLightConnections) serverData).clientReadBuffer.getKeySet()) {
                int dimId = Integer.parseInt(dimStr);
                NBTTagList list = ((DataLightConnections) serverData).clientReadBuffer.getTagList(dimStr, 10);
                Map<BlockPos, List<BlockPos>> connectionMap = clientPosBuffer.get(dimId);
                if(connectionMap == null) {
                    connectionMap = new ConcurrentHashMap<>();
                    clientPosBuffer.put(dimId, connectionMap);
                }
                for (int i = 0; i < list.tagCount(); i++) {
                    NBTTagCompound connection = list.getCompoundTagAt(i);
                    if(connection.hasKey("clear")) {
                        clientPosBuffer.remove(dimId);
                        break;
                    }

                    BlockPos start = BlockPos.fromLong(connection.getLong("sta"));
                    BlockPos end = BlockPos.fromLong(connection.getLong("end"));
                    boolean set = connection.getBoolean("s");
                    List<BlockPos> to = connectionMap.get(start);
                    if(set) {
                        if(to == null) {
                            to = new LinkedList<>();
                            connectionMap.put(start, to);
                        }
                        if(!to.contains(end)) {
                            to.add(end);
                        }
                    } else {
                        if(to != null) {
                            to.remove(end);
                            if(to.isEmpty()) {
                                connectionMap.remove(start);
                            }
                        }
                    }
                }
                if(connectionMap.isEmpty()) {
                    clientPosBuffer.remove(dimId);
                }
            }
        } finally {
            clientReceivingData = false;
        }
    }

    public static class Provider extends ProviderAutoAllocate<DataLightConnections> {

        public Provider(String key) {
            super(key);
        }

        @Override
        public DataLightConnections provideNewInstance() {
            return new DataLightConnections();
        }

    }


}
