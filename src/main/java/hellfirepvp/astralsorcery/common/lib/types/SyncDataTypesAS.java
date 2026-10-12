/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lib.types;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.sync.SyncData;
import hellfirepvp.astralsorcery.common.data.sync.client.*;
import hellfirepvp.astralsorcery.common.data.sync.server.*;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SyncDataTypesAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class SyncDataTypesAS {

    public static final DeferredRegister<SyncData.Type<?, ?, ?, ?>> SYNC_DATA_REGISTER =
            DeferredRegister.create(RegistriesAS.KEY_SYNC_DATA_TYPES, AstralSorcery.MODID);

    public static final DeferredHolder<SyncData.Type<?, ?, ?, ?>, SyncData.Type<FocalPointSyncData, FocalPointSyncData.ClientSync, FocalPointSyncData.ClientDiffSync, FocalPointClientData>> FOCAL_POINT =
            SYNC_DATA_REGISTER.register("focal_point", () ->
                    new SyncData.Type<>(FocalPointSyncData::new, FocalPointClientData::new,
                            FocalPointSyncData.ClientSync.STREAM_CODEC, FocalPointSyncData.ClientDiffSync.STREAM_CODEC));
    public static final DeferredHolder<SyncData.Type<?, ?, ?, ?>, SyncData.Type<LightConnectionSyncData, LightConnectionSyncData.ClientSync, LightConnectionSyncData.ClientDiffSync, LightConnectionClientData>> LIGHT_CONNECTION =
            SYNC_DATA_REGISTER.register("light_connection", () ->
                    new SyncData.Type<>(LightConnectionSyncData::new, LightConnectionClientData::new,
                            LightConnectionSyncData.ClientSync.STREAM_CODEC, LightConnectionSyncData.ClientDiffSync.STREAM_CODEC));
    public static final DeferredHolder<SyncData.Type<?, ?, ?, ?>, SyncData.Type<CelestialGatewaySyncData, CelestialGatewaySyncData.ClientSync, CelestialGatewaySyncData.ClientDiffSync, CelestialGatewayClientData>> CELESTIAL_GATEWAY =
            SYNC_DATA_REGISTER.register("celestial_gateway", () ->
                    new SyncData.Type<>(CelestialGatewaySyncData::new, CelestialGatewayClientData::new,
                            CelestialGatewaySyncData.ClientSync.STREAM_CODEC, CelestialGatewaySyncData.ClientDiffSync.STREAM_CODEC));
    public static final DeferredHolder<SyncData.Type<?, ?, ?, ?>, SyncData.Type<PatreonEntitySyncData, PatreonEntitySyncData.ClientSync, PatreonEntitySyncData.ClientDiffSync, PatreonEntityClientData>> PATREON_ENTITY =
            SYNC_DATA_REGISTER.register("patreon_entity", () ->
                    new SyncData.Type<>(PatreonEntitySyncData::new, PatreonEntityClientData::new,
                            PatreonEntitySyncData.ClientSync.STREAM_CODEC, PatreonEntitySyncData.ClientDiffSync.STREAM_CODEC));
    public static final DeferredHolder<SyncData.Type<?, ?, ?, ?>, SyncData.Type<TimeStopEntitySyncData, TimeStopEntitySyncData.ClientSync, TimeStopEntitySyncData.ClientDiffSync, TimeStopEntityClientData>> TIME_STOP_ENTITIES =
            SYNC_DATA_REGISTER.register("time_stop_entities", () ->
                    new SyncData.Type<>(TimeStopEntitySyncData::new, TimeStopEntityClientData::new,
                            TimeStopEntitySyncData.ClientSync.STREAM_CODEC, TimeStopEntitySyncData.ClientDiffSync.STREAM_CODEC));
    public static final DeferredHolder<SyncData.Type<?, ?, ?, ?>, SyncData.Type<TimeStopZoneSyncData, TimeStopZoneSyncData.ClientSync, TimeStopZoneSyncData.ClientDiffSync, TimeStopZoneClientData>> TIME_STOP_ZONES =
            SYNC_DATA_REGISTER.register("time_stop_zones", () ->
                    new SyncData.Type<>(TimeStopZoneSyncData::new, TimeStopZoneClientData::new,
                            TimeStopZoneSyncData.ClientSync.STREAM_CODEC, TimeStopZoneSyncData.ClientDiffSync.STREAM_CODEC));

}
