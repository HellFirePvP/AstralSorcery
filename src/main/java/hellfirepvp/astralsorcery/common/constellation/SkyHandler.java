/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation;

import com.google.common.collect.Maps;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.world.WorldSeedCache;
import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SkyHandler
 * Created by HellFirePvP
 * Date: 01.07.2019 / 06:59
 */
public class SkyHandler implements ITickHandler {
    
    private static final SkyHandler instance = new SkyHandler();

    private Map<Integer, WorldContext> worldHandlersServer = Maps.newHashMap();
    private Map<Integer, WorldContext> worldHandlersClient = Maps.newHashMap();

    private Map<Integer, Boolean> skyRevertMap = Maps.newHashMap();

    private SkyHandler() {}

    public static SkyHandler getInstance() {
        return instance;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        if (type == TickEvent.Type.WORLD) {
            World w = (World) context[0];
            if (!w.isRemote) {
                int dimId = w.getDimension().getType().getId();
                skyRevertMap.put(dimId, false);
                WorldContext ctx = worldHandlersServer.get(dimId);
                if (ctx == null) {
                    ctx = createContext(MiscUtils.getRandomWorldSeed(w));
                    worldHandlersServer.put(dimId, ctx);
                }
                ctx.tick(w);
            }
        } else {
            handleClientTick();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClientTick() {
        World w = Minecraft.getInstance().world;
        if (w != null) {
            int dimId = w.getDimension().getType().getId();
            WorldContext ctx = worldHandlersClient.get(dimId);
            if (ctx == null) {
                Optional<Long> seedOpt = WorldSeedCache.getSeedIfPresent(w);
                if (!seedOpt.isPresent()) {
                    return;
                }
                ctx = createContext(seedOpt.get());
                worldHandlersClient.put(dimId, ctx);
            }
            ctx.tick(w);
        }
    }

    private WorldContext createContext(long seed) {
        return new WorldContext(seed);
    }

    @Nullable
    public static WorldContext getContext(World world) {
        return getContext(world, world.isRemote() ? LogicalSide.CLIENT : LogicalSide.SERVER);
    }

    @Nullable
    public static WorldContext getContext(World world, LogicalSide dist) {
        if (world == null) {
            return null;
        }
        int dimId = world.getDimension().getType().getId();
        if (dist.isClient()) {
            return getInstance().worldHandlersClient.getOrDefault(dimId, null);
        } else {
            return getInstance().worldHandlersServer.getOrDefault(dimId, null);
        }
    }

    public void revertWorldTimeTick(World world) {
        int dimId = world.getDimension().getType().getId();
        Boolean state = skyRevertMap.get(dimId);
        if (!world.isRemote && state != null && !state) {
            skyRevertMap.put(dimId, true);
            world.setDayTime(world.getDayTime() - 1);
        }
    }

    public void clientClearCache() {
        worldHandlersClient.clear();
    }

    public void informWorldUnload(IWorld world) {
        worldHandlersServer.remove(world.getDimension().getType().getId());
        worldHandlersClient.remove(world.getDimension().getType().getId());
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
