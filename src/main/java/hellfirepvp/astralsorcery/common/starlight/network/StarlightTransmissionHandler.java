/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.network;

import hellfirepvp.observerlib.common.util.tick.ITickHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;

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
    private Map<ResourceLocation, TransmissionWorldHandler> worldHandlers = new HashMap<>();

    private StarlightTransmissionHandler() {}

    public static StarlightTransmissionHandler getInstance() {
        return instance;
    }

    @Override
    public void tick(TickEvent.Type type, Object... context) {
        World world = (World) context[0];
        if (world.isRemote()) {
            return;
        }

        worldHandlers.computeIfAbsent(world.func_234923_W_().func_240901_a_(), TransmissionWorldHandler::new)
                .tick(world);
    }

    public void clearServer() {
        worldHandlers.values().forEach(TransmissionWorldHandler::clear);
        worldHandlers.clear();
    }

    public void informWorldUnload(World world) {
        ResourceLocation dimKey = world.func_234923_W_().func_240901_a_();
        TransmissionWorldHandler handle = worldHandlers.get(dimKey);
        if (handle != null) {
            handle.clear();
        }
        this.worldHandlers.remove(dimKey);
    }

    @Nullable
    public TransmissionWorldHandler getWorldHandler(World world) {
        if (world == null) {
            return null;
        }
        return worldHandlers.get(world.func_234923_W_().func_240901_a_());
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
