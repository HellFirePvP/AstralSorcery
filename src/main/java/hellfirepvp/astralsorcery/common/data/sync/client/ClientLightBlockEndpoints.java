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

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ClientLightBlockEndpoints
 * Created by HellFirePvP
 * Date: 29.08.2019 / 20:10
 */
public class ClientLightBlockEndpoints extends ClientData<ClientLightBlockEndpoints> {

    private final Map<RegistryKey<World>, Set<BlockPos>> clientPositions = new HashMap<>();

    public boolean doesPositionReceiveStarlightClient(World world, BlockPos pos) {
        return this.clientPositions.getOrDefault(world.getDimensionKey(), Collections.emptySet()).contains(pos);
    }

    @Override
    public void clear(RegistryKey<World> dim) {
        this.clientPositions.remove(dim);
    }

    @Override
    public void clearClient() {
        this.clientPositions.clear();
    }

    public static class Reader extends ClientDataReader<ClientLightBlockEndpoints> {

        @Override
        public void readFromIncomingFullSync(ClientLightBlockEndpoints data, CompoundNBT compound) {
            data.clientPositions.clear();

            for (String dimKey : compound.keySet()) {
                RegistryKey<World> dim = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(dimKey));

                Set<BlockPos> positions = new HashSet<>();
                ListNBT list = compound.getList(dimKey, Constants.NBT.TAG_COMPOUND);
                for (INBT iTag : list) {
                    CompoundNBT tag = (CompoundNBT) iTag;

                    BlockPos pos = BlockPos.fromLong(tag.getLong("pos"));
                    positions.add(pos);
                }
                data.clientPositions.put(dim, positions);
            }
        }

        @Override
        public void readFromIncomingDiff(ClientLightBlockEndpoints data, CompoundNBT compound) {
            Set<String> clearedDimensions = new HashSet<>();
            for (INBT dimKeyNBT : compound.getList("clear", Constants.NBT.TAG_STRING)) {
                String dimKey = dimKeyNBT.getString();
                RegistryKey<World> dim = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(dimKey));
                data.clientPositions.remove(dim);

                clearedDimensions.add(dimKey);
            }

            for (String dimKey : compound.keySet()) {
                if (clearedDimensions.contains(dimKey)) {
                    continue;
                }
                RegistryKey<World> dim = RegistryKey.getOrCreateKey(Registry.WORLD_KEY, new ResourceLocation(dimKey));

                Set<BlockPos> positions = data.clientPositions.computeIfAbsent(dim, k -> new HashSet<>());

                ListNBT list = compound.getList(dimKey, Constants.NBT.TAG_COMPOUND);
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
