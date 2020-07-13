/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.data.sync;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.sync.base.AbstractData;
import hellfirepvp.astralsorcery.common.data.sync.base.AbstractDataProvider;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientData;
import hellfirepvp.astralsorcery.common.data.sync.base.ClientDataReader;
import hellfirepvp.astralsorcery.common.data.sync.server.DataLightBlockEndpoints;
import hellfirepvp.astralsorcery.common.data.sync.server.DataLightConnections;
import hellfirepvp.astralsorcery.common.data.sync.server.DataPatreonFlares;
import hellfirepvp.astralsorcery.common.data.sync.server.DataTimeFreezeEffects;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktSyncData;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SyncDataHolder
 * Created by HellFirePvP
 * Date: 07.05.2016 / 01:11
 */
public class SyncDataHolder implements ITickHandler {

    private static final SyncDataHolder tickInstance = new SyncDataHolder();

    public static final ResourceLocation DATA_LIGHT_CONNECTIONS = AstralSorcery.key("connections");
    public static final ResourceLocation DATA_LIGHT_BLOCK_ENDPOINTS = AstralSorcery.key("endpoints");
    public static final ResourceLocation DATA_TIME_FREEZE_EFFECTS = AstralSorcery.key("time_freeze");
    public static final ResourceLocation DATA_PATREON_FLARES = AstralSorcery.key("patreon");

    private static Map<ResourceLocation, AbstractData> serverData = new HashMap<>();
    private static Map<ResourceLocation, ClientData<?>> clientData = new HashMap<>();
    private static Map<ResourceLocation, ClientDataReader<?>> readers = new HashMap<>();

    private static Set<ResourceLocation> dirtyData = new HashSet<>();
    private static final Object lck = new Object();

    private SyncDataHolder() {}

    public static SyncDataHolder getTickInstance() {
        return tickInstance;
    }

    public static void register(AbstractDataProvider<? extends AbstractData, ? extends ClientData> provider) {
        SyncDataRegistry.register(provider);
        serverData.put(provider.getKey(), provider.provideServerData());
        clientData.put(provider.getKey(), provider.provideClientData());
        readers.put(provider.getKey(), provider.createReader());
    }

    public static <T extends AbstractData> void executeServer(ResourceLocation key, Class<T> typeHint, Consumer<T> fct) {
        synchronized (lck) {
            T dat = (T) serverData.get(key);
            if (dat != null) {
                fct.accept(dat);
            }
        }
    }

    public static <T extends ClientData<T>> void executeClient(ResourceLocation key, Class<T> typeHint, Consumer<T> fct) {
        T dat = (T) clientData.get(key);
        if (dat != null) {
            fct.accept(dat);
        }
    }

    @Nullable
    public static <T extends ClientData<T>> ClientDataReader<T> getReader(ResourceLocation key) {
        return (ClientDataReader<T>) readers.get(key);
    }

    public static void markForUpdate(ResourceLocation key) {
        synchronized (lck) {
            dirtyData.add(key);
        }
    }

    public static void clearWorld(IWorld world) {
        DimensionType dimType = world.getDimension().getType();
        for (ResourceLocation key : SyncDataRegistry.getKnownKeys()) {
            if (world.isRemote()) {
                executeClient(key, ClientData.class, data -> data.clear(dimType));
            } else {
                executeServer(key, AbstractData.class, data -> data.clear(dimType));
            }
        }
    }

    public static void clear(LogicalSide side) {
        for (ResourceLocation key : SyncDataRegistry.getKnownKeys()) {
            if (side.isClient()) {
                executeClient(key, ClientData.class, ClientData::clearClient);
            } else {
                executeServer(key, AbstractData.class, AbstractData::clearServer);
            }
        }
    }

    public static void initialize() {
        register(new DataLightConnections.Provider(DATA_LIGHT_CONNECTIONS));
        register(new DataLightBlockEndpoints.Provider(DATA_LIGHT_BLOCK_ENDPOINTS));
        register(new DataTimeFreezeEffects.Provider(DATA_TIME_FREEZE_EFFECTS));
        register(new DataPatreonFlares.Provider(DATA_PATREON_FLARES));
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        if (dirtyData.isEmpty()) {
            return;
        }
        Map<ResourceLocation, CompoundNBT> pktData = new HashMap<>();
        synchronized (lck) {
            for (ResourceLocation key : dirtyData) {
                AbstractData dat = serverData.get(key);
                if (dat != null) {
                    CompoundNBT nbt = new CompoundNBT();
                    dat.writeDiffDataToPacket(nbt);
                    pktData.put(key, nbt);
                }
            }
            dirtyData.clear();
        }
        PktSyncData dataSync = new PktSyncData(pktData);
        PacketChannel.CHANNEL.sendToAll(dataSync);
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.SERVER);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Sync Data Holder";
    }
}
