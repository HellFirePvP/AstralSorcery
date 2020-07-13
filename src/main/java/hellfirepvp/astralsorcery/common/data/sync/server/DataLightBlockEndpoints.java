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
import hellfirepvp.astralsorcery.common.data.sync.client.ClientLightBlockEndpoints;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DataLightBlockEndpoints
 * Created by HellFirePvP
 * Date: 10.08.2016 / 18:30
 */
public class DataLightBlockEndpoints extends AbstractData {

    private Map<DimensionType, Set<BlockPos>> serverPositions = new HashMap<>();

    private Map<DimensionType, Map<BlockPos, Boolean>> serverChangeBuffer = new HashMap<>();
    private Set<DimensionType> dimensionClearBuffer = new HashSet<>();

    private DataLightBlockEndpoints(ResourceLocation key) {
        super(key);
    }

    public void updateNewEndpoint(DimensionType dimType, BlockPos pos) {
        Map<BlockPos, Boolean> posMap = serverChangeBuffer.computeIfAbsent(dimType, k -> new HashMap<>());
        posMap.put(pos, true);

        Set<BlockPos> posBuffer = serverPositions.computeIfAbsent(dimType, k -> new HashSet<>());
        posBuffer.add(pos);
        markDirty();
    }

    public void updateNewEndpoints(DimensionType dimType, Collection<BlockPos> newPositions) {
        Map<BlockPos, Boolean> posMap = serverChangeBuffer.computeIfAbsent(dimType, k -> new HashMap<>());
        for (BlockPos pos : newPositions) {
            posMap.put(pos, true);
        }

        Set<BlockPos> posBuffer = serverPositions.computeIfAbsent(dimType, k -> new HashSet<>());
        posBuffer.addAll(newPositions);
        markDirty();
    }

    public void removeEndpoints(DimensionType dimType, Collection<BlockPos> positions) {
        Map<BlockPos, Boolean> posMap = serverChangeBuffer.computeIfAbsent(dimType, k -> new HashMap<>());
        for (BlockPos pos : positions) {
            posMap.put(pos, false);
        }

        Set<BlockPos> posBuffer = serverPositions.computeIfAbsent(dimType, k -> new HashSet<>());
        if (posBuffer.removeAll(positions)) {
            markDirty();
        }
    }

    public boolean doesPositionReceiveStarlightServer(IWorld world, BlockPos pos) {
        int dim = world.getDimension().getType().getId();
        return this.serverPositions.getOrDefault(dim, Collections.emptySet()).contains(pos);
    }

    @Override
    public void clear(DimensionType dimType) {
        if (this.serverPositions.remove(dimType) != null) {
            this.serverChangeBuffer.remove(dimType);
            this.dimensionClearBuffer.add(dimType);
            markDirty();
        }
    }

    @Override
    public void clearServer() {
        this.dimensionClearBuffer.clear();
        this.serverChangeBuffer.clear();
        this.serverPositions.clear();
    }

    @Override
    public void writeAllDataToPacket(CompoundNBT compound) {
        for (DimensionType dimType : serverPositions.keySet()) {
            Set<BlockPos> dat = serverPositions.get(dimType);

            ListNBT dataList = new ListNBT();
            for (BlockPos pos : dat) {
                CompoundNBT cmp = new CompoundNBT();
                cmp.putLong("pos", pos.toLong());
                dataList.add(cmp);
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

        for (DimensionType dimType : this.serverChangeBuffer.keySet()) {
            if (this.dimensionClearBuffer.contains(dimType)) {
                continue;
            }

            Map<BlockPos, Boolean> data = this.serverChangeBuffer.get(dimType);

            ListNBT dataList = new ListNBT();
            for (BlockPos pos : data.keySet()) {
                CompoundNBT cmp = new CompoundNBT();
                cmp.putLong("pos", pos.toLong());
                cmp.putBoolean("add", data.get(pos));
                dataList.add(cmp);
            }

            compound.put(dimType.getRegistryName().toString(), dataList);
        }

        this.dimensionClearBuffer.clear();
        this.serverChangeBuffer.clear();
    }

    public static class Provider extends AbstractDataProvider<DataLightBlockEndpoints, ClientLightBlockEndpoints> {

        public Provider(ResourceLocation key) {
            super(key);
        }

        @Override
        public DataLightBlockEndpoints provideServerData() {
            return new DataLightBlockEndpoints(getKey());
        }

        @Override
        public ClientLightBlockEndpoints provideClientData() {
            return new ClientLightBlockEndpoints();
        }

        @Override
        public ClientDataReader<ClientLightBlockEndpoints> createReader() {
            return new ClientLightBlockEndpoints.Reader();
        }
    }
}
