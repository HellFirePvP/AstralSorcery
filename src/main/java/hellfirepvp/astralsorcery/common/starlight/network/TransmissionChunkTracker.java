/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.network;

import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.IEventBus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmissionChunkTracker
 * Created by HellFirePvP
 * Date: 05.08.2016 / 10:08
 */
public class TransmissionChunkTracker {

    public static final TransmissionChunkTracker INSTANCE = new TransmissionChunkTracker();

    private TransmissionChunkTracker() {}

    public void attachListeners(IEventBus eventBus) {
        eventBus.addListener(this::onChLoad);
        eventBus.addListener(this::onChUnload);
        eventBus.addListener(this::onWorldLoad);
        eventBus.addListener(this::onWorldUnload);
    }

    private void onChLoad(ChunkEvent.Load event) {
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance().getWorldHandler(event.getWorld());
        if (handle != null) {
            handle.informChunkLoad(event.getChunk().getPos());
        }
    }

    private void onChUnload(ChunkEvent.Unload event) {
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance().getWorldHandler(event.getWorld());
        if (handle != null) {
            handle.informChunkUnload(event.getChunk().getPos());
        }
    }

    private void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld().isRemote()) {
            return;
        }
        StarlightUpdateHandler.getInstance().informWorldLoad(event.getWorld());
    }

    private void onWorldUnload(WorldEvent.Unload event) {
        //StarlightTransmissionHandler.getInstance().informWorldUnload(event.getWorld());
        //StarlightUpdateHandler.getInstance().informWorldUnload(event.getWorld());
    }

}
