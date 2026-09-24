/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.server;

import com.mojang.datafixers.util.Pair;
import hellfirepvp.astralsorcery.common.data.sync.ClientSyncData;
import hellfirepvp.astralsorcery.common.data.sync.ClientSyncDiffData;
import hellfirepvp.astralsorcery.common.data.sync.SyncData;
import hellfirepvp.astralsorcery.common.data.sync.client.LightConnectionClientData;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LightConnectionSyncData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LightConnectionSyncData extends SyncData<LightConnectionSyncData.ClientSync, LightConnectionSyncData.ClientDiffSync, LightConnectionClientData> {

    private final Map<ResourceKey<Level>, Map<BlockPos, Set<BlockPos>>> levelLinks = new HashMap<>();

    private ClientDiffSync diffBuffer = new ClientDiffSync();

    public void addConnections(ResourceKey<Level> dimKey, Set<Pair<BlockPos, BlockPos>> connections) {
        Map<BlockPos, Set<BlockPos>> dimLinks = this.levelLinks.computeIfAbsent(dimKey, k -> new HashMap<>());
        connections.forEach(connection -> {
            dimLinks.computeIfAbsent(connection.getFirst(), k -> new HashSet<>()).add(connection.getSecond());
        });

        this.diffBuffer.addedConnections.computeIfAbsent(dimKey, k -> new HashSet<>()).addAll(connections);
        this.markDirty();
    }

    public void removeConnections(ResourceKey<Level> dimKey, Set<Pair<BlockPos, BlockPos>> connections) {
        Map<BlockPos, Set<BlockPos>> dimLinks = this.levelLinks.get(dimKey);
        if (dimLinks == null) return;
        connections.forEach(connection -> {
            Set<BlockPos> endpoints = dimLinks.get(connection.getFirst());
            if (endpoints != null) {
                endpoints.remove(connection.getSecond());
                if (endpoints.isEmpty()) {
                    dimLinks.remove(connection.getFirst());
                }
            }
        });

        this.diffBuffer.removedConnections.computeIfAbsent(dimKey, k -> new HashSet<>()).addAll(connections);
        this.markDirty();
    }

    @Override
    public void clearLevel(ServerLevel sLevel) {
        ResourceKey<Level> dimKey = sLevel.dimension();

        this.levelLinks.remove(dimKey);
        this.diffBuffer.clearedDimensions.add(dimKey);
        this.markDirty();
    }

    @Override
    public void clearAll() {
        this.levelLinks.clear();
        this.diffBuffer = new ClientDiffSync();
    }

    @Override
    public ClientSync syncAllData() {
        return new ClientSync(this.levelLinks);
    }

    @Override
    public ClientDiffSync syncDiffData() {
        ClientDiffSync bufferedDiff = this.diffBuffer;
        this.diffBuffer = new ClientDiffSync();
        return bufferedDiff;
    }

    @Override
    public Type<?, ClientSync, ClientDiffSync, LightConnectionClientData> getType() {
        return SyncDataTypesAS.LIGHT_CONNECTION.get();
    }

    public static class ClientSync extends ClientSyncData<LightConnectionClientData> {

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientSync> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(size -> new HashMap<>(), ResourceKey.streamCodec(Registries.DIMENSION),
                        ByteBufCodecs.map(size2 -> new HashMap<>(), BlockPos.STREAM_CODEC,
                                BlockPos.STREAM_CODEC.apply(SetCodec.streamOp()))),
                syncData -> syncData.fullLinks,
                ClientSync::new
        );

        private final Map<ResourceKey<Level>, Map<BlockPos, Set<BlockPos>>> fullLinks = new HashMap<>();

        private ClientSync(Map<ResourceKey<Level>, Map<BlockPos, Set<BlockPos>>> fullLinks) {
            this.fullLinks.putAll(fullLinks);
        }

        @Override
        public void updateClientData(LightConnectionClientData dataOut) {
            dataOut.receiveServerData(this.fullLinks);
        }

        @Override
        public Type<?, ?, ?, LightConnectionClientData> type() {
            return SyncDataTypesAS.LIGHT_CONNECTION.get();
        }
    }

    public static class ClientDiffSync extends ClientSyncDiffData<LightConnectionClientData> {

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientDiffSync> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(size -> new HashMap<>(), ResourceKey.streamCodec(Registries.DIMENSION),
                        CodecUtil.pairStreamCodec(BlockPos.STREAM_CODEC).apply(SetCodec.streamOp())),
                diffData -> diffData.addedConnections,
                ByteBufCodecs.map(size -> new HashMap<>(), ResourceKey.streamCodec(Registries.DIMENSION),
                        CodecUtil.pairStreamCodec(BlockPos.STREAM_CODEC).apply(SetCodec.streamOp())),
                diffData -> diffData.removedConnections,
                ResourceKey.streamCodec(Registries.DIMENSION).apply(SetCodec.streamOp()),
                diffData -> diffData.clearedDimensions,
                ClientDiffSync::new
        );

        private final Map<ResourceKey<Level>, Set<Pair<BlockPos, BlockPos>>> addedConnections = new HashMap<>();
        private final Map<ResourceKey<Level>, Set<Pair<BlockPos, BlockPos>>> removedConnections = new HashMap<>();
        private final Set<ResourceKey<Level>> clearedDimensions = new HashSet<>();

        private ClientDiffSync() {}

        private ClientDiffSync(
                Map<ResourceKey<Level>, Set<Pair<BlockPos, BlockPos>>> addedConnections,
                Map<ResourceKey<Level>, Set<Pair<BlockPos, BlockPos>>> removedConnections,
                Set<ResourceKey<Level>> clearedDimensions) {
            this.addedConnections.putAll(addedConnections);
            this.removedConnections.putAll(removedConnections);
            this.clearedDimensions.addAll(clearedDimensions);
        }

        @Override
        public void updateClientData(LightConnectionClientData dataOut) {
            dataOut.receiveDiffData(this.addedConnections, this.removedConnections, this.clearedDimensions);
        }

        @Override
        public Type<?, ?, ?, LightConnectionClientData> type() {
            return SyncDataTypesAS.LIGHT_CONNECTION.get();
        }
    }

}
