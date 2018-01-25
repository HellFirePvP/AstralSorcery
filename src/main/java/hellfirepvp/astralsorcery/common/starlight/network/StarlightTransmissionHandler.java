/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.network;

import hellfirepvp.astralsorcery.common.auxiliary.tick.ITickHandler;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: StarlightTransmissionHandler
 * Created by HellFirePvP
 * Date: 04.08.2016 / 23:24
 */
public class StarlightTransmissionHandler implements ITickHandler {

    private static final StarlightTransmissionHandler instance = new StarlightTransmissionHandler();
    private Map<Integer, TransmissionWorldHandler> worldHandlers = new HashMap<>();

    private StarlightTransmissionHandler() {}

    public static StarlightTransmissionHandler getInstance() {
        return instance;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        World world = (World) context[0];
        if(world.isRemote) return;

        int dimId = world.provider.getDimension();
        TransmissionWorldHandler handle = worldHandlers.get(dimId);
        if(handle == null) {
            handle = new TransmissionWorldHandler(world);
            worldHandlers.put(dimId, handle);
        }
        handle.tick();
    }

    public void serverCleanHandlers() {
        for (int id : worldHandlers.keySet()) {
            worldHandlers.get(id).clear(id);
        }
        worldHandlers.clear();
    }

    public void informWorldUnload(World world) {
        int dimId = world.provider.getDimension();
        TransmissionWorldHandler handle = worldHandlers.get(dimId);
        if(handle != null) {
            handle.clear(world.provider.getDimension());
        }
        this.worldHandlers.remove(dimId);
    }

    @Nullable
    public TransmissionWorldHandler getWorldHandler(World world) {
        if(world == null) return null;
        return worldHandlers.get(world.provider.getDimension());
    }

    @Override
    public EnumSet<TickEvent.Type> getHandledTypes() {
        return EnumSet.of(TickEvent.Type.WORLD);
    }

    @Override
    public boolean canFire(TickEvent.Phase phase) {
        return phase == TickEvent.Phase.END;
    }

    @Override
    public String getName() {
        return "Starlight Transmission Handler";
    }

}
