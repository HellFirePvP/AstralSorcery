/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.server;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.sync.ClientSyncData;
import hellfirepvp.astralsorcery.common.data.sync.ClientSyncDiffData;
import hellfirepvp.astralsorcery.common.data.sync.SyncData;
import hellfirepvp.astralsorcery.common.data.sync.client.FocalPointClientData;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.focal.node.FocalPointNode;
import hellfirepvp.astralsorcery.common.util.data.DiffEntry;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: FocalPointSyncData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class FocalPointSyncData extends SyncData<FocalPointSyncData.ClientSync, FocalPointSyncData.ClientDiffSync, FocalPointClientData> {

    private final Map<ResourceKey<Level>, LevelData> levelData = new HashMap<>();
    private ClientDiffSync dirtyBuffer = new ClientDiffSync();

    private Optional<LevelData> getLevelData(ResourceKey<Level> levelKey) {
        return Optional.ofNullable(this.levelData.get(levelKey));
    }

    private Optional<LevelData> getOrCreateLevelData(ResourceKey<Level> levelKey) {
        return Optional.of(this.levelData.computeIfAbsent(levelKey, key -> new LevelData()));
    }

    public List<FocalPointNode> getNodes(ResourceKey<Level> levelKey) {
        return this.getLevelData(levelKey)
                .map(data -> Collections.unmodifiableList(data.loadedFocalPoints))
                .orElse(List.of());
    }

    public void unloadNodes(ServerLevel sLevel, ChunkPos chunkPos) {
        ResourceKey<Level> levelKey = sLevel.dimension();

        this.getLevelData(levelKey).ifPresent(data -> {
            List<FocalPointNode> focalPoints = data.focalPointsByChunk.remove(chunkPos);
            if (focalPoints != null) {
                focalPoints.forEach(node -> {
                    node.onUnload(sLevel);
                    data.loadedFocalPoints.remove(node);
                    this.dirtyBuffer.addNodeChange(levelKey, node, DiffEntry.Type.REMOVAL);
                });
                if (!focalPoints.isEmpty()) {
                    this.markDirty();
                }
            }
        });
    }

    public void loadNodes(ServerLevel sLevel, List<FocalPointNode> nodes) {
        this.getOrCreateLevelData(sLevel.dimension()).ifPresent(data -> {
            nodes.forEach(node -> {
                this.registerNode(data, sLevel, node);
            });
        });
    }

    public void loadNode(ServerLevel sLevel, FocalPointNode node) {
        this.getOrCreateLevelData(sLevel.dimension()).ifPresent(data -> {
            this.registerNode(data, sLevel, node);
        });
    }

    private void registerNode(LevelData levelData, ServerLevel sLevel, FocalPointNode node) {
        ResourceKey<Level> levelKey = sLevel.dimension();
        ChunkPos chunkPos = node.getPos().toChunkPos();
        List<FocalPointNode> chunkNodes = levelData.focalPointsByChunk.computeIfAbsent(chunkPos, pos -> new ArrayList<>());

        if (chunkNodes.contains(node)) {
            AstralSorcery.LOG.warn("Tried to load duplicate focal point node {} in chunk {} for level {}.",
                    node.getPos(), chunkPos, levelKey.location());
            return;
        }
        chunkNodes.add(node);

        levelData.loadedFocalPoints.add(node);
        node.onLoad(sLevel);
        this.dirtyBuffer.addNodeChange(levelKey, node, DiffEntry.Type.ADDITION);
        this.markDirty();
    }

    public void markForUpdate(ServerLevel sLevel, FocalPointNode node) {
        this.getLevelData(sLevel.dimension()).ifPresent(levelData -> {
            if (levelData.loadedFocalPoints.contains(node)) {
                this.dirtyBuffer.addNodeChange(sLevel.dimension(), node, DiffEntry.Type.UPDATE);
            }
            this.markDirty();
        });
    }

    @Override
    public void clearLevel(ServerLevel sLevel) {
        ResourceKey<Level> levelKey = sLevel.dimension();

        this.getLevelData(levelKey).ifPresent(data ->
                data.loadedFocalPoints.forEach(node -> node.onUnload(sLevel)));
        this.levelData.remove(levelKey);
        this.dirtyBuffer.levelClearBuffer.add(levelKey);
        this.markDirty();
    }

    @Override
    public void clearAll() {
        this.levelData.clear();
        this.dirtyBuffer = new ClientDiffSync();
    }

    @Override
    public ClientSync syncAllData() {
        return new ClientSync(this.levelData);
    }

    @Override
    public ClientDiffSync syncDiffData() {
        ClientDiffSync currentDiff = this.dirtyBuffer;
        this.dirtyBuffer = new ClientDiffSync();
        return currentDiff;
    }

    @Override
    public Type<FocalPointSyncData, ClientSync, ClientDiffSync, FocalPointClientData> getType() {
        return SyncDataTypesAS.FOCAL_POINT.get();
    }

    private static class LevelData {

        private static final StreamCodec<RegistryFriendlyByteBuf, LevelData> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(HashMap::new,
                        ByteBufCodecs.VAR_LONG.map(ChunkPos::new, ChunkPos::toLong),
                        FocalPointNode.STREAM_CODEC.apply(ByteBufCodecs.list())),
                levelData -> levelData.focalPointsByChunk,
                LevelData::new);

        private final Map<ChunkPos, List<FocalPointNode>> focalPointsByChunk = new HashMap<>();
        private final List<FocalPointNode> loadedFocalPoints = new ArrayList<>();

        private LevelData() {}

        private LevelData(Map<ChunkPos, List<FocalPointNode>> focalPointsByChunk) {
            this.focalPointsByChunk.putAll(focalPointsByChunk);
            this.loadedFocalPoints.addAll(focalPointsByChunk.values().stream()
                    .flatMap(List::stream)
                    .toList());
        }
    }

    public static class ClientSync extends ClientSyncData<FocalPointClientData> {

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientSync> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(HashMap::new,
                        ResourceKey.streamCodec(Registries.DIMENSION),
                        LevelData.STREAM_CODEC),
                data -> data.levelData,
                ClientSync::new);

        private final Map<ResourceKey<Level>, LevelData> levelData;

        private ClientSync(Map<ResourceKey<Level>, LevelData> levelData) {
            this.levelData = levelData;
        }

        @Override
        public void updateClientData(FocalPointClientData dataOut) {
            Map<ResourceKey<Level>, Map<ChunkPos, List<FocalPointNode>>> unpacked = this.levelData.entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().focalPointsByChunk));
            this.getClientData().receiveServerData(unpacked);
        }

        @Override
        public Type<?, ?, ?, FocalPointClientData> type() {
            return SyncDataTypesAS.FOCAL_POINT.get();
        }
    }

    public static class ClientDiffSync extends ClientSyncDiffData<FocalPointClientData> {

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientDiffSync> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(Registries.DIMENSION).apply(SetCodec.streamOp()),
                data -> data.levelClearBuffer,
                ByteBufCodecs.map(HashMap::new,
                        ResourceKey.streamCodec(Registries.DIMENSION),
                        DiffEntry.streamCodec(FocalPointNode.STREAM_CODEC).apply(ByteBufCodecs.list())),
                data -> data.nodeChanges,
                ClientDiffSync::new);

        private final Set<ResourceKey<Level>> levelClearBuffer = new HashSet<>();
        private final Map<ResourceKey<Level>, List<DiffEntry<FocalPointNode>>> nodeChanges = new HashMap<>();

        private ClientDiffSync() {}

        private ClientDiffSync(Set<ResourceKey<Level>> levelClearBuffer,
                               Map<ResourceKey<Level>, List<DiffEntry<FocalPointNode>>> nodeChanges) {
            this.levelClearBuffer.addAll(levelClearBuffer);
            this.nodeChanges.putAll(nodeChanges);
        }

        private void addNodeChange(ResourceKey<Level> levelKey, FocalPointNode node, DiffEntry.Type type) {
            this.nodeChanges.computeIfAbsent(levelKey, key -> new ArrayList<>())
                    .add(new DiffEntry<>(node, type));
        }

        @Override
        public void updateClientData(FocalPointClientData dataOut) {
            dataOut.receiveDiffData(this.levelClearBuffer, this.nodeChanges);
        }

        @Override
        public Type<?, ?, ?, FocalPointClientData> type() {
            return SyncDataTypesAS.FOCAL_POINT.get();
        }
    }
}
