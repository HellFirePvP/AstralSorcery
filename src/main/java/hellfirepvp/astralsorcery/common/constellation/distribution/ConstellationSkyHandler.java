/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.distribution;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.packet.client.PktRequestSeed;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ConstellationSkyHandler
 * Created by HellFirePvP
 * Date: 16.11.2016 / 20:47
 */
public class ConstellationSkyHandler implements ITickHandler {

    //For effect purposes to determine how long those events are/last
    public static final int SOLAR_ECLIPSE_HALF_DUR = 2400;
    public static final int LUNAR_ECLIPSE_HALF_DUR = 2400;

    private static final ConstellationSkyHandler instance = new ConstellationSkyHandler();
    private static int activeSession = 0;

    private Map<Integer, Long> cacheSeedLookup = new HashMap<>();

    private Map<Integer, WorldSkyHandler> worldHandlersServer  = new HashMap<>();
    private Map<Integer, WorldSkyHandler> worldHandlersClient  = new HashMap<>();

    private ConstellationSkyHandler() {}

    public static ConstellationSkyHandler getInstance() {
        return instance;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        if(type == TickEvent.Type.WORLD) {
            World w = (World) context[0];
            if(!w.isRemote) {
                WorldSkyHandler handle = worldHandlersServer.get(w.provider.getDimension());
                if(handle == null) {
                    handle = new WorldSkyHandler(new Random(w.getSeed()).nextLong());
                    worldHandlersServer.put(w.provider.getDimension(), handle);
                }
                handle.tick(w);
            }
        } else {
            handleClientTick();
        }
    }

    @SideOnly(Side.CLIENT)
    private void handleClientTick() {
        World w = Minecraft.getMinecraft().world;
        if(w != null) {
            WorldSkyHandler handle = worldHandlersClient.get(w.provider.getDimension());
            if(handle == null) {
                int dim = w.provider.getDimension();
                long seed;
                if(cacheSeedLookup.containsKey(dim)) {
                    seed = cacheSeedLookup.get(dim);
                } else {
                    PktRequestSeed req = new PktRequestSeed(activeSession, dim);
                    PacketChannel.CHANNEL.sendToServer(req);
                    return;
                }
                handle = new WorldSkyHandler(seed);
                worldHandlersClient.put(dim, handle);
            }
            handle.tick(w);
        }
    }

    public void updateSeedCache(int dimId, int session, long seed) {
        if(activeSession == session) {
            cacheSeedLookup.put(dimId, seed);
        }
    }

    @SideOnly(Side.CLIENT)
    public Optional<Long> getSeedIfPresent(World world) {
        return getSeedIfPresent(world.provider.getDimension());
    }

    @SideOnly(Side.CLIENT)
    public Optional<Long> getSeedIfPresent(int dim) {
        if(!cacheSeedLookup.containsKey(dim)) {
            PktRequestSeed req = new PktRequestSeed(activeSession, dim);
            PacketChannel.CHANNEL.sendToServer(req);
            return Optional.empty();
        }
        return Optional.of(cacheSeedLookup.get(dim));
    }

    //Convenience method
    public float getCurrentDaytimeDistribution(World world) {
        float dayPart = world.getWorldTime() % 24000;
        if(dayPart < 11000) return 0F;
        if(dayPart < 15000) return (dayPart - 11000F) / 4000F;
        if(dayPart > 20000) return 1F - (dayPart - 20000F) / 4000F;
        return 1F;
    }

    public boolean isNight(World world) {
        return getCurrentDaytimeDistribution(world) >= 0.65;
    }

    public boolean isDay(World world) {
        return !isNight(world);
    }

    @Nullable
    public WorldSkyHandler getWorldHandler(World world) {
        Map<Integer, WorldSkyHandler> handlerMap;
        if(world.isRemote) {
            handlerMap = worldHandlersClient;
        } else {
            handlerMap = worldHandlersServer;
        }
        return handlerMap.get(world.provider.getDimension());
    }

    public void clientClearCache() {
        activeSession++;
        cacheSeedLookup.clear();
        worldHandlersClient.clear();
    }

    public void informWorldUnload(World world) {
        worldHandlersServer.remove(world.provider.getDimension());
        worldHandlersClient.remove(world.provider.getDimension());
        cacheSeedLookup    .remove(world.provider.getDimension());
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.WORLD, TickEvent.Type.CLIENT);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "ConstellationSkyhandler";
    }

}
