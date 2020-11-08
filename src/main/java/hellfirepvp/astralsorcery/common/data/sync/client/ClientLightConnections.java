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
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientLightConnections
 * Created by HellFirePvP
 * Date: 27.08.2019 / 16:32
 */
public class ClientLightConnections extends ClientData<ClientLightConnections> {

    private final Map<RegistryKey<World>, Map<BlockPos, Set<BlockPos>>> clientPosBuffer = new HashMap<>();

    @Nonnull
    public Map<BlockPos, Set<BlockPos>> getClientConnections(RegistryKey<World> dim) {
        return this.clientPosBuffer.getOrDefault(dim, new HashMap<>());
    }

    @Override
    public void clear(RegistryKey<World> dim) {
        this.clientPosBuffer.remove(dim);
    }

    @Override
    public void clearClient() {
        this.clientPosBuffer.clear();
    }

    public static class Reader extends ClientDataReader<ClientLightConnections> {

        @Override
        public void readFromIncomingFullSync(ClientLightConnections cl, CompoundNBT compound) {
            cl.clientPosBuffer.clear();

            for (String dimKey : compound.keySet()) {
                RegistryKey<World> dim = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(dimKey));

                Map<BlockPos, Set<BlockPos>> posMap = new HashMap<>();
                ListNBT list = compound.getList(dimKey, Constants.NBT.TAG_COMPOUND);
                for (INBT iTag : list) {
                    CompoundNBT tag = (CompoundNBT) iTag;

                    BlockPos start = BlockPos.fromLong(tag.getLong("start"));
                    BlockPos end   = BlockPos.fromLong(tag.getLong("end"));
                    posMap.computeIfAbsent(start, s -> new HashSet<>())
                            .add(end);
                }

                cl.clientPosBuffer.put(dim, posMap);
            }
        }

        @Override
        public void readFromIncomingDiff(ClientLightConnections cl, CompoundNBT compound) {
            Set<String> clearedDimensions = new HashSet<>();
            for (INBT dimKeyNBT : compound.getList("clear", Constants.NBT.TAG_STRING)) {
                String dimKey = dimKeyNBT.getString();
                RegistryKey<World> dim = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(dimKey));
                cl.clientPosBuffer.remove(dim);

                clearedDimensions.add(dimKey);
            }

            for (String dimKey : compound.keySet()) {
                if (clearedDimensions.contains(dimKey)) {
                    continue;
                }
                RegistryKey<World> dim = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(dimKey));

                Map<BlockPos, Set<BlockPos>> posMap = cl.clientPosBuffer.computeIfAbsent(dim, d -> new HashMap<>());

                ListNBT list = compound.getList(dimKey, Constants.NBT.TAG_COMPOUND);
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
