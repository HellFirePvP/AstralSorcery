/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary.gateway;

import hellfirepvp.astralsorcery.common.data.world.GatewayCache;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktUpdateGateways;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CelestialGatewayHandler
 * Created by HellFirePvP
 * Date: 23.08.2019 / 22:15
 */
public class CelestialGatewayHandler {

    public static final CelestialGatewayHandler INSTANCE = new CelestialGatewayHandler();
    private CelestialGatewayFilter filter = null;
    private boolean startUp = false;

    private Map<ResourceLocation, List<GatewayCache.GatewayNode>> serverCache = new HashMap<>();
    private Map<ResourceLocation, List<GatewayCache.GatewayNode>> clientCache = new HashMap<>();

    private CelestialGatewayHandler() {}

    private CelestialGatewayFilter getFilter() {
        if (filter == null) {
            filter = new CelestialGatewayFilter();
        }
        return filter;
    }

    public void onServerStart() {
        startUp = true;
        CelestialGatewayFilter filter = getFilter();
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        DimensionManager.getRegistry().stream()
                .filter(DimensionManager::keepLoaded)
                .forEach(type -> {
                    if (!filter.hasGateways(type.getRegistryName())) {
                        return;
                    }
                    loadIntoCache(server.getWorld(type));
                });
        startUp = false;
    }

    public void onWorldInit(WorldEvent.Load event) {
        if (this.startUp) {
            return; //We're already loading up there.
        }

        IWorld world = event.getWorld();
        if (world.isRemote()) {
            return;
        }

        this.loadIntoCache(world);

        PktUpdateGateways pkt = new PktUpdateGateways(this.getGatewayCache(LogicalSide.SERVER));
        PacketChannel.CHANNEL.sendToAll(pkt);
    }

    public List<GatewayCache.GatewayNode> getGatewaysForWorld(IWorld world, LogicalSide side) {
        return (side == LogicalSide.SERVER ? serverCache : clientCache).get(world.getDimension().getType().getRegistryName());
    }

    public Map<ResourceLocation, List<GatewayCache.GatewayNode>> getGatewayCache(LogicalSide side) {
        return Collections.unmodifiableMap(side == LogicalSide.SERVER ? serverCache : clientCache);
    }

    @OnlyIn(Dist.CLIENT)
    public void updateClientCache(Map<ResourceLocation, List<GatewayCache.GatewayNode>> positions) {
        this.clientCache = positions;
    }

    private void loadIntoCache(IWorld world) {
        GatewayCache cache = DataAS.DOMAIN_AS.getData(world, DataAS.KEY_GATEWAY_CACHE);
        this.serverCache.put(world.getDimension().getType().getRegistryName(), cache.getGatewayPositions());
    }

}
