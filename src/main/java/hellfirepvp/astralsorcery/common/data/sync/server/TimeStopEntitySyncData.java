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
import hellfirepvp.astralsorcery.common.data.sync.client.TimeStopEntityClientData;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TimeStopEntitySyncData
 * Created by HellFirePvP
 * Date: 09.10.2026 / 13:55
 */
public class TimeStopEntitySyncData extends SyncData<TimeStopEntitySyncData.ClientSync, TimeStopEntitySyncData.ClientDiffSync, TimeStopEntityClientData> {

    private final Map<ResourceKey<Level>, Set<Integer>> serverEntityFreeze = new HashMap<>();
    private ClientDiffSync dirtyBuffer = new ClientDiffSync();

    public void freeze(Entity entity) {
        ResourceKey<Level> key = entity.level().dimension();
        if (this.serverEntityFreeze.computeIfAbsent(key, k -> new HashSet<>()).add(entity.getId())) {
            this.dirtyBuffer.add(entity);
            this.markDirty();
        }
    }

    public void unfreeze(Entity entity) {
        ResourceKey<Level> key = entity.level().dimension();
        if (this.serverEntityFreeze.computeIfAbsent(key, k -> new HashSet<>()).remove(entity.getId())) {
            this.dirtyBuffer.remove(entity);
            this.markDirty();
        }
    }

    public boolean isFrozen(Entity entity) {
        return this.serverEntityFreeze.getOrDefault(entity.level().dimension(), Collections.emptySet()).contains(entity.getId());
    }

    @Override
    public void clearLevel(ServerLevel sLevel) {
        this.serverEntityFreeze.remove(sLevel.dimension());
    }

    @Override
    public void clearAll() {
        this.serverEntityFreeze.clear();
        this.dirtyBuffer = new ClientDiffSync();
    }

    @Override
    public ClientSync syncAllData() {
        return new ClientSync(Map.copyOf(this.serverEntityFreeze));
    }

    @Override
    public ClientDiffSync syncDiffData() {
        ClientDiffSync sync = this.dirtyBuffer;
        this.dirtyBuffer = new ClientDiffSync();
        return sync;
    }

    @Override
    public Type<?, ClientSync, ClientDiffSync, TimeStopEntityClientData> getType() {
        return SyncDataTypesAS.TIME_STOP_ENTITIES.get();
    }

    public static class ClientDiffSync extends ClientSyncDiffData<TimeStopEntityClientData> {

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientDiffSync> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(HashMap::new,
                        ResourceKey.streamCodec(Registries.DIMENSION),
                        ByteBufCodecs.map(HashMap::new,
                                ByteBufCodecs.INT,
                                ByteBufCodecs.BOOL)),
                data -> data.updates,
                ClientDiffSync::new
        );

        private Map<ResourceKey<Level>, Map<Integer, Boolean>> updates = new HashMap<>();

        private ClientDiffSync() {}

        private ClientDiffSync(Map<ResourceKey<Level>, Map<Integer, Boolean>> updates) {
            this.updates = updates;
        }

        private void add(Entity entity) {
            this.updates.computeIfAbsent(entity.level().dimension(), key -> new HashMap<>())
                    .put(entity.getId(), true);
        }

        private void remove(Entity entity) {
            this.updates.computeIfAbsent(entity.level().dimension(), key -> new HashMap<>())
                    .put(entity.getId(), false);
        }

        @Override
        public void updateClientData(TimeStopEntityClientData dataOut) {
            dataOut.syncDiff(this.updates);
        }

        @Override
        public SyncData.Type<?, ?, ?, TimeStopEntityClientData> type() {
            return SyncDataTypesAS.TIME_STOP_ENTITIES.get();
        }
    }

    public static class ClientSync extends ClientSyncData<TimeStopEntityClientData> {

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientSync> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(HashMap::new,
                        ResourceKey.streamCodec(Registries.DIMENSION),
                        ByteBufCodecs.INT.apply(SetCodec.streamOp())),
                data -> data.entities,
                ClientSync::new
        );

        private final Map<ResourceKey<Level>, Set<Integer>> entities;

        private ClientSync(Map<ResourceKey<Level>, Set<Integer>> entities) {
            this.entities = entities;
        }

        @Override
        public void updateClientData(TimeStopEntityClientData dataOut) {
            dataOut.syncAll(this.entities);
        }

        @Override
        public SyncData.Type<?, ?, ?, TimeStopEntityClientData> type() {
            return SyncDataTypesAS.TIME_STOP_ENTITIES.get();
        }
    }

}
