package hellfirepvp.astralsorcery.common.base;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.world.WorldCacheManager;
import hellfirepvp.astralsorcery.common.data.world.data.GatewayCache;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CelestialGatewaySystem
 * Created by HellFirePvP
 * Date: 16.04.2017 / 20:18
 */
public class CelestialGatewaySystem {

    private boolean startup = false;

    public static CelestialGatewaySystem instance = new CelestialGatewaySystem();
    private Map<Integer, List<BlockPos>> serverCache = new HashMap<>();

    private CelestialGatewaySystem() {}

    public void onServerStart() {
        startup = true;
        Integer[] worlds = DimensionManager.getStaticDimensionIDs(); //Should be loaded during startup = we should grab those.
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        for (Integer id : worlds) {
            if(id == null) continue;
            WorldServer world = server.worldServerForDimension(id);
            loadWorldCache(world);
        }
        startup = false;
    }

    @SubscribeEvent
    public void onWorldInit(WorldEvent.Load event) {
        if(startup) return; //We're already loading up there.

        World world = event.getWorld();
        if(world.isRemote) return;

        loadWorldCache(world);
    }

    public void addPosition(World world, BlockPos pos) {
        if(world.isRemote) return;

        int dim = world.provider.getDimension();
        if(!serverCache.containsKey(dim)) {
            forceLoad(dim);
        }
        if(!serverCache.containsKey(dim)) {
            AstralSorcery.log.info("Couldn't add position for world " + dim + "! - Force loading the world resulted in... nothing.");
            return;
        }

        List<BlockPos> cache = serverCache.get(dim);
        if(!cache.contains(pos)) {
            cache.add(pos);
        }
    }

    public void removePosition(World world, BlockPos pos) {
        if(world.isRemote) return;

        int dim = world.provider.getDimension();
        if(!serverCache.containsKey(dim)) {
            return;
        }
        serverCache.get(dim).remove(pos);
    }

    private void forceLoad(int dim) {
        WorldServer serv = DimensionManager.getWorld(dim);
        if(serv == null) {
            DimensionManager.initDimension(dim);
        }
    }

    private void loadWorldCache(World world) {
        GatewayCache cache = WorldCacheManager.getOrLoadData(world, WorldCacheManager.SaveKey.GATEWAY_DATA);
        serverCache.put(world.provider.getDimension(), cache.getGatewayPositions());
    }

}
