/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data;

import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataLightBlockEndpoints
 * Created by HellFirePvP
 * Date: 10.08.2016 / 18:30
 */
public class DataLightBlockEndpoints extends AbstractData {

    private Map<Integer, List<BlockPos>> clientPositions = new HashMap<>();
    private Map<Integer, List<BlockPos>> serverPositions = new HashMap<>();

    private Map<Integer, LinkedList<Tuple<BlockPos, Boolean>>> serverChangeBuffer = new HashMap<>();

    private final Object lock = new Object();

    private NBTTagCompound clientReadBuffer = new NBTTagCompound();

    public void updateNewEndpoint(int dimId, BlockPos pos) {
        synchronized (lock) {
            LinkedList<Tuple<BlockPos, Boolean>> list = serverChangeBuffer.get(dimId);
            if(list == null) {
                list = new LinkedList<>();
                serverChangeBuffer.put(dimId, list);
            }
            list.add(new Tuple<>(pos, true));

            List<BlockPos> posBuffer = serverPositions.get(dimId);
            if(posBuffer == null) {
                posBuffer = new LinkedList<>();
                serverPositions.put(dimId, posBuffer);
            }
            posBuffer.add(pos);
        }
        markDirty();
    }

    public void updateNewEndpoints(int dimId, List<BlockPos> newPositions) {
        synchronized (lock) {
            LinkedList<Tuple<BlockPos, Boolean>> list = serverChangeBuffer.get(dimId);
            if(list == null) {
                list = new LinkedList<>();
                serverChangeBuffer.put(dimId, list);
            }
            for (BlockPos pos : newPositions) {
                list.add(new Tuple<>(pos, true));
            }

            List<BlockPos> posBuffer = serverPositions.get(dimId);
            if(posBuffer == null) {
                posBuffer = new LinkedList<>();
                serverPositions.put(dimId, posBuffer);
            }
            posBuffer.addAll(newPositions);
        }
        markDirty();
    }

    public void removeEndpoints(int dimId, List<BlockPos> positions) {
        synchronized (lock) {
            LinkedList<Tuple<BlockPos, Boolean>> list = serverChangeBuffer.get(dimId);
            if(list == null) {
                list = new LinkedList<>();
                serverChangeBuffer.put(dimId, list);
            }
            for (BlockPos pos : positions) {
                list.add(new Tuple<>(pos, false));
            }

            List<BlockPos> posBuffer = serverPositions.get(dimId);
            if(posBuffer == null) {
                posBuffer = new LinkedList<>();
                serverPositions.put(dimId, posBuffer);
            }
            posBuffer.removeAll(positions);
        }
        markDirty();
    }

    public void clearDimensionEndpoints(int dimId) {
        serverPositions.remove(dimId);
        synchronized (lock) {
            serverChangeBuffer.remove(dimId);
            serverChangeBuffer.put(dimId, new LinkedList<>());
            serverChangeBuffer.get(dimId).add(new Tuple<>(null, false));
        }
        markDirty();
    }

    public boolean doesPositionReceiveStarlightClient(World world, BlockPos pos) {
        int dim = world.provider.getDimension();
        return clientPositions.containsKey(dim) && clientPositions.get(dim).contains(pos);
    }

    public boolean doesPositionReceiveStarlightServer(World world, BlockPos pos) {
        int dim = world.provider.getDimension();
        return serverPositions.containsKey(dim) && serverPositions.get(dim).contains(pos);
    }

    public void clientClean() {
        this.clientPositions.clear();
    }

    @Override
    public void writeAllDataToPacket(NBTTagCompound compound) {
        for (int dimId : serverPositions.keySet()) {
            List<BlockPos> dat = serverPositions.get(dimId);
            NBTTagList dataList = new NBTTagList();
            for (BlockPos pos : dat) {
                NBTTagCompound cmp = new NBTTagCompound();
                cmp.setLong("pos", pos.toLong());
                cmp.setBoolean("s", true);
                dataList.appendTag(cmp);
            }

            compound.setTag("" + dimId, dataList);
        }
    }

    @Override
    public void writeToPacket(NBTTagCompound compound) {
        synchronized (lock) {
            for (int dimId : serverChangeBuffer.keySet()) {
                LinkedList<Tuple<BlockPos, Boolean>> changes = serverChangeBuffer.get(dimId);
                if(!changes.isEmpty()) {
                    NBTTagList list = new NBTTagList();
                    for (Tuple<BlockPos, Boolean> tpl : changes) {
                        if(tpl.key == null) {
                            list = new NBTTagList();
                            NBTTagCompound cm = new NBTTagCompound();
                            cm.setBoolean("clear", true);
                            list.appendTag(cm);
                            break;
                        }

                        NBTTagCompound cmp = new NBTTagCompound();
                        cmp.setLong("pos", tpl.key.toLong());
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
        if(!(serverData instanceof DataLightBlockEndpoints)) return;

        for (String dimStr : ((DataLightBlockEndpoints) serverData).clientReadBuffer.getKeySet()) {
            int dimId = Integer.parseInt(dimStr);
            NBTTagList list = ((DataLightBlockEndpoints) serverData).clientReadBuffer.getTagList(dimStr, 10);
            List<BlockPos> positions = clientPositions.get(dimId);
            if(positions == null) {
                positions = new LinkedList<>();
                clientPositions.put(dimId, positions);
            }
            for (int i = 0; i < list.tagCount(); i++) {
                NBTTagCompound connection = list.getCompoundTagAt(i);
                if(connection.hasKey("clear")) {
                    clientPositions.remove(dimId);
                    break;
                }

                BlockPos position = BlockPos.fromLong(connection.getLong("pos"));
                boolean set = connection.getBoolean("s");
                if(set) {
                    positions.add(position);
                } else {
                    positions.remove(position);
                }
            }
            if(positions.isEmpty()) {
                clientPositions.remove(dimId);
            }
        }
    }

    public static class Provider extends ProviderAutoAllocate<DataLightBlockEndpoints> {

        public Provider(String key) {
            super(key);
        }

        @Override
        public DataLightBlockEndpoints provideNewInstance() {
            return new DataLightBlockEndpoints();
        }

    }

}
