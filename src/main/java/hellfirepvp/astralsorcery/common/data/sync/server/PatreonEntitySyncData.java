/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.server;

import hellfirepvp.astralsorcery.common.data.sync.ClientSyncData;
import hellfirepvp.astralsorcery.common.data.sync.ClientSyncDiffData;
import hellfirepvp.astralsorcery.common.data.sync.SyncData;
import hellfirepvp.astralsorcery.common.data.sync.client.PatreonEntityClientData;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.patreon.PatreonEffect;
import hellfirepvp.astralsorcery.common.patreon.entity.PatreonPartialEntity;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PatreonEntitySyncData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PatreonEntitySyncData extends SyncData<PatreonEntitySyncData.ClientSync, PatreonEntitySyncData.ClientDiffSync, PatreonEntityClientData> {

    private final Map<UUID, Set<PatreonPartialEntity>> serverEntities = new HashMap<>();

    private ClientDiffSync dirtyBuffer = new ClientDiffSync();

    @Nullable
    public PatreonPartialEntity create(Player player, PatreonEffect effect) {
        UUID playerUUID = player.getUUID();
        PatreonPartialEntity.Provider provider = effect.getPartialEntityProvider();
        if (provider == null) return null;
        PatreonPartialEntity entity = provider.serverProvider().apply(playerUUID);
        if (entity == null) return null;

        entity.placeNear(player);

        this.serverEntities.computeIfAbsent(playerUUID, id -> new HashSet<>()).add(entity);

        this.dirtyBuffer.addChange(playerUUID, effect.getEffectUUID(), entity);
        this.markDirty();
        return entity;
    }

    public void addUpdate(Player player, UUID effectUUID, PatreonPartialEntity entity) {
        this.dirtyBuffer.addChange(player.getUUID(), effectUUID, entity);
        this.markDirty();
    }

    public void remove(UUID playerUUID) {
        this.dirtyBuffer.addRemoval(playerUUID);
        this.serverEntities.remove(playerUUID);
        this.markDirty();
    }

    public Set<UUID> getOwners() {
        return this.serverEntities.keySet();
    }

    public Set<PatreonPartialEntity> getEntities(UUID playerUUID) {
        return this.serverEntities.getOrDefault(playerUUID, Collections.emptySet());
    }

    @Override
    public void clearLevel(ServerLevel sLevel) {}

    @Override
    public void clearAll() {
        this.serverEntities.clear();
        this.dirtyBuffer = new ClientDiffSync();
    }

    @Override
    public ClientSync syncAllData() {
        return new ClientSync().addAllEntities(this.serverEntities);
    }

    @Override
    public ClientDiffSync syncDiffData() {
        ClientDiffSync sync = this.dirtyBuffer;
        this.dirtyBuffer = new ClientDiffSync();
        return sync;
    }

    @Override
    public Type<?, ClientSync, ClientDiffSync, PatreonEntityClientData> getType() {
        return SyncDataTypesAS.PATREON_ENTITY.get();
    }

    public static class ClientSync extends ClientSyncData<PatreonEntityClientData> {

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientSync> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(size -> new HashMap<>(),
                        UUIDUtil.STREAM_CODEC,
                        ByteBufCodecs.map(size -> new HashMap<>(),
                                UUIDUtil.STREAM_CODEC,
                                PatreonPartialEntity.UpdateInfo.STREAM_CODEC)),
                sync -> sync.flareUpdateMap,
                ClientSync::new);

        private final Map<UUID, Map<UUID, PatreonPartialEntity.UpdateInfo>> flareUpdateMap = new HashMap<>();

        private ClientSync() {}

        private ClientSync(Map<UUID, Map<UUID, PatreonPartialEntity.UpdateInfo>> flareUpdateMap) {
            this.flareUpdateMap.putAll(flareUpdateMap);
        }

        private ClientSync addAllEntities(Map<UUID, Set<PatreonPartialEntity>> serverEntities) {
            serverEntities.forEach((playerUUID, entities) -> {
                Map<UUID, PatreonPartialEntity.UpdateInfo> effectMap = new HashMap<>();
                entities.forEach(e -> effectMap.put(e.getEffectUUID(), e.getUpdateInfo()));
                this.flareUpdateMap.put(playerUUID, effectMap);
            });
            return this;
        }

        @Override
        public void updateClientData(PatreonEntityClientData dataOut) {
            dataOut.receiveChanges(Collections.emptySet(), this.flareUpdateMap);
        }

        @Override
        public SyncData.Type<?, ?, ?, PatreonEntityClientData> type() {
            return SyncDataTypesAS.PATREON_ENTITY.get();
        }
    }

    public static class ClientDiffSync extends ClientSyncDiffData<PatreonEntityClientData> {

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientDiffSync> STREAM_CODEC = StreamCodec.composite(
                UUIDUtil.STREAM_CODEC.apply(SetCodec.streamOp()),
                sync -> sync.flareRemovals,
                ByteBufCodecs.map(size -> new HashMap<>(),
                        UUIDUtil.STREAM_CODEC,
                        ByteBufCodecs.map(size -> new HashMap<>(),
                                UUIDUtil.STREAM_CODEC,
                                PatreonPartialEntity.UpdateInfo.STREAM_CODEC)),
                sync -> sync.flareUpdateMap,
                ClientDiffSync::new);

        private final Set<UUID> flareRemovals = new HashSet<>();
        private final Map<UUID, Map<UUID, PatreonPartialEntity.UpdateInfo>> flareUpdateMap = new HashMap<>();

        private ClientDiffSync() {}

        private ClientDiffSync(Set<UUID> flareRemovals, Map<UUID, Map<UUID, PatreonPartialEntity.UpdateInfo>> flareUpdateMap) {
            this.flareRemovals.addAll(flareRemovals);
            this.flareUpdateMap.putAll(flareUpdateMap);
        }

        public void addChange(UUID playerUUID, UUID effectUUID, PatreonPartialEntity entity) {
            this.flareRemovals.remove(playerUUID);

            PatreonPartialEntity.UpdateInfo snapshot = entity.getUpdateInfo();
            this.flareUpdateMap.computeIfAbsent(playerUUID, p -> new HashMap<>()).put(effectUUID, snapshot);
        }

        public void addRemoval(UUID playerUUID) {
            this.flareRemovals.add(playerUUID);
            this.flareUpdateMap.remove(playerUUID);
        }

        @Override
        public void updateClientData(PatreonEntityClientData dataOut) {
            dataOut.receiveChanges(this.flareRemovals, this.flareUpdateMap);
        }

        @Override
        public SyncData.Type<?, ?, ?, PatreonEntityClientData> type() {
            return SyncDataTypesAS.PATREON_ENTITY.get();
        }
    }
}
