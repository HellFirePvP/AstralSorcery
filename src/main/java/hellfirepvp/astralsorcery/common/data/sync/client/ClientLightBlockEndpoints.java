/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
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
import net.minecraft.nbt.IntNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
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

    private Map<Integer, Set<BlockPos>> clientPositions = new HashMap<>();


    public boolean doesPositionReceiveStarlightClient(IWorld world, BlockPos pos) {
        int dim = world.getDimension().getType().getId();
        return this.clientPositions.getOrDefault(dim, Collections.emptySet()).contains(pos);
    }

    @Override
    public void clear(int dimId) {
        this.clientPositions.remove(dimId);
    }

    @Override
    public void clearClient() {
        this.clientPositions.clear();
    }

    public static class Reader extends ClientDataReader<ClientLightBlockEndpoints> {

        @Override
        public void readFromIncomingFullSync(ClientLightBlockEndpoints data, CompoundNBT compound) {
            data.clientPositions.clear();

            for (String dimIdKey : compound.keySet()) {
                int dimId;
                try {
                    dimId = Integer.parseInt(dimIdKey);
                } catch (NumberFormatException exc) {
                    continue;
                }

                Set<BlockPos> positions = new HashSet<>();
                ListNBT list = compound.getList(dimIdKey, Constants.NBT.TAG_COMPOUND);
                for (INBT iTag : list) {
                    CompoundNBT tag = (CompoundNBT) iTag;

                    BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));
                    positions.add(pos);
                }
                data.clientPositions.put(dimId, positions);
            }
        }

        @Override
        public void readFromIncomingDiff(ClientLightBlockEndpoints data, CompoundNBT compound) {
            for (INBT dimIdNbt : compound.getList("clear", Constants.NBT.TAG_INT)) {
                int dimId = ((IntNBT) dimIdNbt).getInt();
                data.clientPositions.remove(dimId);
            }

            for (String dimIdKey : compound.keySet()) {
                int dimId;
                try {
                    dimId = Integer.parseInt(dimIdKey);
                } catch (NumberFormatException exc) {
                    continue; //Skip wrongly formatted dimensionids
                }

                Set<BlockPos> positions = data.clientPositions.computeIfAbsent(dimId, k -> new HashSet<>());

                ListNBT list = compound.getList(dimIdKey, Constants.NBT.TAG_COMPOUND);
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
