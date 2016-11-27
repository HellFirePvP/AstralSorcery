package hellfirepvp.astralsorcery.common.constellation.distribution;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import hellfirepvp.astralsorcery.common.data.DataWorldSkyHandlers;
import hellfirepvp.astralsorcery.common.data.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

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

    private Map<Integer, WorldSkyHandler> worldHandlers = new HashMap<>();

    private ConstellationSkyHandler() {}

    public static ConstellationSkyHandler getInstance() {
        return instance;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        if(type == TickEvent.Type.WORLD) {
            World w = (World) context[0];
            if(DataWorldSkyHandlers.hasWorldHandler(w.provider.getDimension(), Side.SERVER)) {
                WorldSkyHandler handle = worldHandlers.get(w.provider.getDimension());
                if(handle == null) {
                    handle = new WorldSkyHandler(w);
                    worldHandlers.put(w.provider.getDimension(), handle);
                }
                handle.tick(w);
            }
        } else {
            handleClientTick();
        }
    }

    @SideOnly(Side.CLIENT)
    private void handleClientTick() {
        World w = Minecraft.getMinecraft().theWorld;
        if(w != null && DataWorldSkyHandlers.hasWorldHandler(w.provider.getDimension(), Side.CLIENT)) {
            WorldSkyHandler handle = worldHandlers.get(w.provider.getDimension());
            if(handle == null) {
                handle = new WorldSkyHandler(w);
                worldHandlers.put(w.provider.getDimension(), handle);
            }
            handle.tick(w);
        }
    }

    //Convenience method
    public float getCurrentDaytimeDistribution(World world) {
        WorldSkyHandler handle = ConstellationSkyHandler.getInstance().getWorldHandler(world);
        if(handle != null) {
            return handle.getCurrentDaytimeDistribution(world);
        }
        return 0.1F;
    }

    public void resetIterationsClient() {
        worldHandlers.clear();
    }

    @Nullable
    public WorldSkyHandler getWorldHandler(World world) {
        return worldHandlers.get(world.provider.getDimension());
    }

    public void informWorldUnload(World world) {
        worldHandlers.remove(world.provider.getDimension());
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
