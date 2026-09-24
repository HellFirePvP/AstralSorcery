/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync.server;

import hellfirepvp.astralsorcery.common.data.level.CelestialGatewayData;
import hellfirepvp.astralsorcery.common.data.sync.ClientSyncData;
import hellfirepvp.astralsorcery.common.data.sync.ClientSyncDiffData;
import hellfirepvp.astralsorcery.common.data.sync.SyncData;
import hellfirepvp.astralsorcery.common.data.sync.SyncDataManager;
import hellfirepvp.astralsorcery.common.data.sync.client.CelestialGatewayClientData;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.lib.types.SyncDataTypesAS;
import hellfirepvp.astralsorcery.common.util.codec.SetCodec;
import hellfirepvp.astralsorcery.common.util.data.DiffEntry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.level.LevelEvent;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CelestialGatewaySyncData
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CelestialGatewaySyncData extends SyncData<CelestialGatewaySyncData.ClientSync, CelestialGatewaySyncData.ClientDiffSync, CelestialGatewayClientData> {

    private final Map<ResourceKey<Level>, Set<CelestialGatewayData.GatewayEntry>> levelGatewayEntries = new HashMap<>();
    private ClientDiffSync dirtyBuffer = new ClientDiffSync();

    public void addEntry(Level level, CelestialGatewayData.GatewayEntry entry) {
        ResourceKey<Level> levelKey = level.dimension();
        Set<CelestialGatewayData.GatewayEntry> entries =
                this.levelGatewayEntries.computeIfAbsent(levelKey, k -> new HashSet<>());

        if (entries.add(entry)) {
            this.dirtyBuffer.addChange(levelKey, entry, DiffEntry.Type.ADDITION);
            this.markDirty();
        }
    }

    public void removeEntry(Level level, CelestialGatewayData.GatewayEntry entry) {
        ResourceKey<Level> levelKey = level.dimension();
        Set<CelestialGatewayData.GatewayEntry> entries = this.levelGatewayEntries.get(levelKey);
        if (entries != null && entries.remove(entry)) {
            this.dirtyBuffer.addChange(levelKey, entry, DiffEntry.Type.REMOVAL);
            this.markDirty();
        }
    }

    public static void attachListeners(IEventBus bus) {
        bus.addListener(CelestialGatewaySyncData::onWorldLoad);
    }

    private static void onWorldLoad(LevelEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel sLevel)) return;

        CelestialGatewaySyncData data = SyncDataManager.getInstance().getData(SyncDataTypesAS.CELESTIAL_GATEWAY);
        DataAS.DOMAIN_AS.getData(sLevel, DataAS.KEY_CELESTIAL_GATEWAY_DATA).getGatewayEntries()
                .forEach(entry -> data.addEntry(sLevel, entry));
    }

    @Override
    public void clearLevel(ServerLevel sLevel) {
        this.levelGatewayEntries.remove(sLevel.dimension());
        this.dirtyBuffer.removedLevels.add(sLevel.dimension());
        this.markDirty();
    }

    @Override
    public void clearAll() {
        this.levelGatewayEntries.clear();
        this.dirtyBuffer = new ClientDiffSync();
    }

    @Override
    public ClientSync syncAllData() {
        return new ClientSync(this.levelGatewayEntries);
    }

    @Override
    public ClientDiffSync syncDiffData() {
        ClientDiffSync sync = this.dirtyBuffer;
        this.dirtyBuffer = new ClientDiffSync();
        return sync;
    }

    @Override
    public Type<?, ClientSync, ClientDiffSync, CelestialGatewayClientData> getType() {
        return SyncDataTypesAS.CELESTIAL_GATEWAY.get();
    }

    public static class ClientSync extends ClientSyncData<CelestialGatewayClientData> {

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientSync> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.map(HashMap::new,
                        ResourceKey.streamCodec(Registries.DIMENSION),
                        CelestialGatewayData.GatewayEntry.STREAM_CODEC.apply(SetCodec.streamOp())),
                sync -> sync.allLevelGatewayEntries,
                ClientSync::new
        );

        private final Map<ResourceKey<Level>, Set<CelestialGatewayData.GatewayEntry>> allLevelGatewayEntries = new HashMap<>();

        private ClientSync(Map<ResourceKey<Level>, Set<CelestialGatewayData.GatewayEntry>> allLevelGatewayEntries) {
            this.allLevelGatewayEntries.putAll(allLevelGatewayEntries);
        }

        @Override
        public void updateClientData(CelestialGatewayClientData dataOut) {
            dataOut.receiveAll(this.allLevelGatewayEntries);
        }

        @Override
        public Type<?, ?, ?, CelestialGatewayClientData> type() {
            return SyncDataTypesAS.CELESTIAL_GATEWAY.get();
        }
    }

    public static class ClientDiffSync extends ClientSyncDiffData<CelestialGatewayClientData> {

        public static final StreamCodec<RegistryFriendlyByteBuf, ClientDiffSync> STREAM_CODEC = StreamCodec.composite(
                ResourceKey.streamCodec(Registries.DIMENSION).apply(SetCodec.streamOp()),
                data -> data.removedLevels,
                ByteBufCodecs.map(HashMap::new,
                        ResourceKey.streamCodec(Registries.DIMENSION),
                        DiffEntry.streamCodec(CelestialGatewayData.GatewayEntry.STREAM_CODEC).apply(ByteBufCodecs.list())),
                data -> data.entryChanges,
                ClientDiffSync::new);


        private final Set<ResourceKey<Level>> removedLevels = new HashSet<>();
        private final Map<ResourceKey<Level>, List<DiffEntry<CelestialGatewayData.GatewayEntry>>> entryChanges = new HashMap<>();

        private void addChange(ResourceKey<Level> levelKey, CelestialGatewayData.GatewayEntry entry, DiffEntry.Type type) {
            this.entryChanges.computeIfAbsent(levelKey, k -> new ArrayList<>())
                    .add(new DiffEntry<>(entry, type));
        }

        private ClientDiffSync() {}

        private ClientDiffSync(Set<ResourceKey<Level>> removedLevels,
                               Map<ResourceKey<Level>, List<DiffEntry<CelestialGatewayData.GatewayEntry>>> entryChanges) {
            this.removedLevels.addAll(removedLevels);
            this.entryChanges.putAll(entryChanges);
        }

        @Override
        public void updateClientData(CelestialGatewayClientData dataOut) {
            dataOut.receiveChanges(this.removedLevels, this.entryChanges);
        }

        @Override
        public Type<?, ?, ?, CelestialGatewayClientData> type() {
            return SyncDataTypesAS.CELESTIAL_GATEWAY.get();
        }
    }
}
