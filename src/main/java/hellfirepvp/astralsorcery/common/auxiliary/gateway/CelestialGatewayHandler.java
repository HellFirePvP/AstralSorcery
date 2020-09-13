/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.auxiliary.gateway;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.world.GatewayCache;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.play.server.PktUpdateGateways;
import hellfirepvp.astralsorcery.common.util.SidedReference;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nullable;
import java.util.*;

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

    private final SidedReference<Map<ResourceLocation, List<GatewayCache.GatewayNode>>> cache = new SidedReference<>();

    private CelestialGatewayHandler() {}

    private CelestialGatewayFilter getFilter() {
        if (filter == null) {
            filter = new CelestialGatewayFilter();
        }
        return filter;
    }

    public void addPosition(IWorld world, GatewayCache.GatewayNode node) {
        if (world.isRemote()) {
            return;
        }

        ResourceLocation dimKey = world.getDimension().getType().getRegistryName();
        if (!cache.getData(LogicalSide.SERVER).map(map -> map.get(dimKey)).isPresent()) {
            forceLoad(world.getDimension().getType());
        }

        Optional<List<GatewayCache.GatewayNode>> worldData = cache.getData(LogicalSide.SERVER).map(map -> map.get(dimKey));
        if (!worldData.isPresent()) {
            AstralSorcery.log.info("Couldn't add gateway at " + node.getPos() + " - loading the world failed.");
            return;
        }
        List<GatewayCache.GatewayNode> nodes = worldData.get();

        getFilter().addDim(dimKey);
        if (!nodes.contains(node)) {
            nodes.add(node);
            syncToAll();
        }
    }

    public void removePosition(IWorld world, BlockPos pos) {
        if (world.isRemote()) {
            return;
        }

        ResourceLocation dimKey = world.getDimension().getType().getRegistryName();
        Optional<List<GatewayCache.GatewayNode>> worldData = cache.getData(LogicalSide.SERVER).map(map -> map.get(dimKey));
        if (!worldData.isPresent()) {
            return;
        }
        List<GatewayCache.GatewayNode> nodes = worldData.get();
        if (nodes.removeIf(node -> node.getPos().equals(pos))) {
            if (nodes.isEmpty()) {
                getFilter().removeDim(dimKey);
            }
            syncToAll();
        }
    }

    private void forceLoad(DimensionType type) {
        MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        srv.getWorld(type);
    }

    public void onServerStart() {
        startUp = true;
        CelestialGatewayFilter filter = getFilter();
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        DimensionManager.getRegistry().stream()
                .forEach(type -> {
                    if (!filter.hasGateways(type.getRegistryName())) {
                        return;
                    }
                    loadIntoCache(server.getWorld(type));
                });
        startUp = false;
    }

    public void onServerStop() {
        this.cache.setData(LogicalSide.SERVER, null);
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
        this.syncToAll();
    }

    public void syncToAll() {
        PktUpdateGateways pkt = new PktUpdateGateways(this.getGatewayCache(LogicalSide.SERVER));
        PacketChannel.CHANNEL.sendToAll(pkt);
    }

    public List<GatewayCache.GatewayNode> getGatewaysForWorld(IWorld world, LogicalSide side) {
        return this.cache.getData(side)
                .map(data -> data.getOrDefault(world.getDimension().getType().getRegistryName(), Collections.emptyList()))
                .orElse(Collections.emptyList());
    }

    public Map<ResourceLocation, List<GatewayCache.GatewayNode>> getGatewayCache(LogicalSide side) {
        return this.cache.getData(side).orElse(Collections.emptyMap());
    }

    @OnlyIn(Dist.CLIENT)
    public void updateClientCache(@Nullable Map<ResourceLocation, List<GatewayCache.GatewayNode>> positions) {
        this.cache.setData(LogicalSide.CLIENT, positions);
    }

    private void loadIntoCache(IWorld world) {
        GatewayCache cache = DataAS.DOMAIN_AS.getData(world, DataAS.KEY_GATEWAY_CACHE);
        Map<ResourceLocation, List<GatewayCache.GatewayNode>> gatewayCache = this.cache.getData(LogicalSide.SERVER).orElse(new HashMap<>());
        gatewayCache.put(world.getDimension().getType().getRegistryName(), cache.getGatewayPositions());
        this.cache.setData(LogicalSide.SERVER, gatewayCache);
    }

}
