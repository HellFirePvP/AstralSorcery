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
import hellfirepvp.astralsorcery.common.data.sync.client.TimeStopZoneClientData;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import hellfirepvp.astralsorcery.common.util.time.TimeStopZone;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TimeStopZoneSyncData
 * Created by HellFirePvP
 * Date: 12.10.2026 / 22:12
 */
public class TimeStopZoneSyncData extends SyncData<TimeStopZoneSyncData.ClientSync, TimeStopZoneSyncData.ClientDiffSync, TimeStopZoneClientData> {

    private final Map<ResourceKey<Level>, Set<TimeStopZone.Snapshot>> serverFreezeZones = new HashMap<>();
    private ClientDiffSync dirtyBuffer = new ClientDiffSync();

    public void update(ServerLevel sLevel, TimeStopZone zone) {
        this.update(sLevel, zone.snapshot());
    }

    public void update(ServerLevel sLevel, TimeStopZone.Snapshot snapshot) {
        Set<TimeStopZone.Snapshot> zones = this.serverFreezeZones.computeIfAbsent(sLevel.dimension(), key -> new HashSet<>());
        zones.removeIf(zone -> zone.id().equals(snapshot.id()));
        zones.add(snapshot);
        this.dirtyBuffer.add(sLevel, snapshot);
        this.markDirty();
    }

    public void remove(ServerLevel sLevel, TimeStopZone zone) {
        this.remove(sLevel, zone.snapshot());
    }

    public void remove(ServerLevel sLevel, TimeStopZone.Snapshot snapshot) {
        Set<TimeStopZone.Snapshot> zones = this.serverFreezeZones.computeIfAbsent(sLevel.dimension(), key -> new HashSet<>());
        if (zones.removeIf(zone -> zone.id().equals(snapshot.id()))) {
            this.dirtyBuffer.remove(sLevel, snapshot);
            this.markDirty();
        }
    }

    @Override
    public void clearLevel(ServerLevel sLevel) {
        this.serverFreezeZones.remove(sLevel.dimension());
    }

    @Override
    public void clearAll() {
        this.serverFreezeZones.clear();
        this.dirtyBuffer = new ClientDiffSync();
    }

    @Override
    public ClientSync syncAllData() {
        return new ClientSync(this.serverFreezeZones);
    }

    @Override
    public ClientDiffSync syncDiffData() {
        ClientDiffSync sync = this.dirtyBuffer;
        this.dirtyBuffer = new ClientDiffSync();
        return sync;
    }

    @Override
    public Type<?, ClientSync, ClientDiffSync, TimeStopZoneClientData> getType() {
        return SyncDataTypesAS.TIME_STOP_ZONES.get();
    }


    public static class ClientDiffSync extends ClientSyncDiffData<TimeStopZoneClientData> {

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientDiffSync> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(HashMap::new,
                        ResourceKey.streamCodec(Registries.DIMENSION),
                        ByteBufCodecs.map(HashMap::new,
                                TimeStopZone.Snapshot.STREAM_CODEC,
                                ByteBufCodecs.BOOL)),
                data -> data.updates,
                ClientDiffSync::new
        );

        private Map<ResourceKey<Level>, Map<TimeStopZone.Snapshot, Boolean>> updates = new HashMap<>();

        private ClientDiffSync() {}

        private ClientDiffSync(Map<ResourceKey<Level>, Map<TimeStopZone.Snapshot, Boolean>> updates) {
            this.updates = updates;
        }

        private void add(ServerLevel sLevel, TimeStopZone.Snapshot zone) {
            Map<TimeStopZone.Snapshot, Boolean> map = this.updates.computeIfAbsent(sLevel.dimension(), key -> new HashMap<>());
            map.keySet().removeIf(snapshot -> snapshot.id().equals(zone.id()));
            map.put(zone, true);
        }

        private void remove(ServerLevel sLevel, TimeStopZone.Snapshot zone) {
            Map<TimeStopZone.Snapshot, Boolean> map = this.updates.computeIfAbsent(sLevel.dimension(), key -> new HashMap<>());
            map.keySet().removeIf(snapshot -> snapshot.id().equals(zone.id()));
            map.put(zone, false);
        }

        @Override
        public void updateClientData(TimeStopZoneClientData dataOut) {
            dataOut.syncDiff(this.updates);
        }

        @Override
        public SyncData.Type<?, ?, ?, TimeStopZoneClientData> type() {
            return SyncDataTypesAS.TIME_STOP_ZONES.get();
        }
    }

    public static class ClientSync extends ClientSyncData<TimeStopZoneClientData> {

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientSync> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(HashMap::new,
                        ResourceKey.streamCodec(Registries.DIMENSION),
                        TimeStopZone.Snapshot.STREAM_CODEC.apply(SetCodec.streamOp())),
                data -> data.zones,
                ClientSync::new
        );

        private final Map<ResourceKey<Level>, Set<TimeStopZone.Snapshot>> zones;

        private ClientSync(Map<ResourceKey<Level>, Set<TimeStopZone.Snapshot>> zones) {
            this.zones = zones;
        }

        @Override
        public void updateClientData(TimeStopZoneClientData dataOut) {
            dataOut.syncAll(this.zones);
        }

        @Override
        public SyncData.Type<?, ?, ?, TimeStopZoneClientData> type() {
            return SyncDataTypesAS.TIME_STOP_ZONES.get();
        }
    }

}
