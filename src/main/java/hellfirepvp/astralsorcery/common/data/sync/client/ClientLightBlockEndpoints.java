/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.client;

import hellfirepvp.astralsorcery.common.data.sync.base.ClientData;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientDataReader;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.util.Constants;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientLightBlockEndpoints
 * Created by HellFirePvP
 * Date: 29.08.2019 / 20:10
 */
public class ClientLightBlockEndpoints extends ClientData<ClientLightBlockEndpoints> {

    private Map<DimensionType, Set<BlockPos>> clientPositions = new HashMap<>();


    public boolean doesPositionReceiveStarlightClient(IWorld world, BlockPos pos) {
        return this.clientPositions.getOrDefault(world.getDimension().getType(), Collections.emptySet()).contains(pos);
    }

    @Override
    public void clear(DimensionType dimType) {
        this.clientPositions.remove(dimType);
    }

    @Override
    public void clearClient() {
        this.clientPositions.clear();
    }

    public static class Reader extends ClientDataReader<ClientLightBlockEndpoints> {

        @Override
        public void readFromIncomingFullSync(ClientLightBlockEndpoints data, CompoundNBT compound) {
            data.clientPositions.clear();

            for (String dimTypeKey : compound.keySet()) {
                DimensionType type = DimensionManager.getRegistry().getValue(new ResourceLocation(dimTypeKey)).orElse(null);
                if (type == null) {
                    continue;
                }

                Set<BlockPos> positions = new HashSet<>();
                ListNBT list = compound.getList(dimTypeKey, Constants.NBT.TAG_COMPOUND);
                for (INBT iTag : list) {
                    CompoundNBT tag = (CompoundNBT) iTag;

                    BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));
                    positions.add(pos);
                }
                data.clientPositions.put(type, positions);
            }
        }

        @Override
        public void readFromIncomingDiff(ClientLightBlockEndpoints data, CompoundNBT compound) {
            Set<String> clearedDimensions = new HashSet<>();
            for (INBT dimKeyNBT : compound.getList("clear", Constants.NBT.TAG_STRING)) {
                String dimKey = dimKeyNBT.getString();
                DimensionType type = DimensionManager.getRegistry()
                        .getValue(new ResourceLocation(dimKey)).orElse(null);
                if (type != null) {
                    data.clientPositions.remove(type);
                }

                clearedDimensions.add(dimKey);
            }

            for (String dimTypeKey : compound.keySet()) {
                if (clearedDimensions.contains(dimTypeKey)) {
                    continue;
                }
                DimensionType type = DimensionManager.getRegistry().getValue(new ResourceLocation(dimTypeKey)).orElse(null);
                if (type == null) {
                    continue;
                }

                Set<BlockPos> positions = data.clientPositions.computeIfAbsent(type, k -> new HashSet<>());

                ListNBT list = compound.getList(dimTypeKey, Constants.NBT.TAG_COMPOUND);
                for (INBT iTag : list) {
                    CompoundNBT tag = (CompoundNBT) iTag;

                    BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));
                    boolean addNew = tag.getBoolean("add");

                    if (addNew) {
                        positions.add(pos);
                    } else {
                        positions.remove(pos);
                    }
                }
            }
        }
    }
}
