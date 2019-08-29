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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientLightConnections
 * Created by HellFirePvP
 * Date: 27.08.2019 / 16:32
 */
public class ClientLightConnections extends ClientData<ClientLightConnections> {

    private Map<Integer, Map<BlockPos, Set<BlockPos>>> clientPosBuffer = new HashMap<>();

    @Nonnull
    public Map<BlockPos, Set<BlockPos>> getClientConnections(int dimId) {
        return this.clientPosBuffer.getOrDefault(dimId, new HashMap<>());
    }

    @Override
    public void clear(int dimId) {
        this.clientPosBuffer.remove(dimId);
    }

    @Override
    public void clearClient() {
        this.clientPosBuffer.clear();
    }

    public static class Reader extends ClientDataReader<ClientLightConnections> {

        @Override
        public void readFromIncomingFullSync(ClientLightConnections cl, CompoundNBT compound) {
            cl.clientPosBuffer.clear();

            for (String dimIdKey : compound.keySet()) {
                int dimId;
                try {
                    dimId = Integer.parseInt(dimIdKey);
                } catch (NumberFormatException exc) {
                    continue; //Skip wrongly formatted dimensionids
                }

                Map<BlockPos, Set<BlockPos>> posMap = new HashMap<>();
                ListNBT list = compound.getList(dimIdKey, Constants.NBT.TAG_COMPOUND);
                for (INBT iTag : list) {
                    CompoundNBT tag = (CompoundNBT) iTag;

                    BlockPos start = BlockPos.fromLong(tag.getLong("start"));
                    BlockPos end   = BlockPos.fromLong(tag.getLong("end"));
                    posMap.computeIfAbsent(start, s -> new HashSet<>())
                            .add(end);
                }

                cl.clientPosBuffer.put(dimId, posMap);
            }
        }

        @Override
        public void readFromIncomingDiff(ClientLightConnections cl, CompoundNBT compound) {
            for (INBT dimIdNbt : compound.getList("clear", Constants.NBT.TAG_INT)) {
                int dimId = ((IntNBT) dimIdNbt).getInt();
                cl.clientPosBuffer.remove(dimId);
            }

            for (String dimIdKey : compound.keySet()) {
                int dimId;
                try {
                    dimId = Integer.parseInt(dimIdKey);
                } catch (NumberFormatException exc) {
                    continue; //Skip wrongly formatted dimensionids
                }

                Map<BlockPos, Set<BlockPos>> posMap = cl.clientPosBuffer.computeIfAbsent(dimId, d -> new HashMap<>());

                ListNBT list = compound.getList(dimIdKey, Constants.NBT.TAG_COMPOUND);
                for (INBT iTag : list) {
                    CompoundNBT tag = (CompoundNBT) iTag;

                    BlockPos start = BlockPos.fromLong(tag.getLong("start"));
                    BlockPos end = BlockPos.fromLong(tag.getLong("end"));
                    boolean newConnection = tag.getBoolean("connect");

                    if (newConnection) {
                        posMap.computeIfAbsent(start, s -> new HashSet<>())
                                .add(end);
                    } else {
                        Set<BlockPos> endPoints = posMap.get(start);
                        if (endPoints != null &&
                                endPoints.remove(end) &&
                                endPoints.isEmpty()) {

                            posMap.remove(start);
                        }
                    }
                }
            }
        }
    }
}
