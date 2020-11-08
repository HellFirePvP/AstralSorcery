/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.network;

import net.minecraft.world.IWorld;
import net.minecraft.world.World;
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
        IWorld iWorld = event.getWorld();
        if (iWorld.isRemote() || !(iWorld instanceof World)) {
            return;
        }
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance().getWorldHandler((World) iWorld);
        if (handle != null) {
            handle.informChunkLoad(event.getChunk().getPos());
        }
    }

    private void onChUnload(ChunkEvent.Unload event) {
        IWorld iWorld = event.getWorld();
        if (iWorld.isRemote() || !(iWorld instanceof World)) {
            return;
        }
        TransmissionWorldHandler handle = StarlightTransmissionHandler.getInstance().getWorldHandler((World) iWorld);
        if (handle != null) {
            handle.informChunkUnload(event.getChunk().getPos());
        }
    }

    private void onWorldLoad(WorldEvent.Load event) {
        IWorld iWorld = event.getWorld();
        if (iWorld.isRemote() || !(iWorld instanceof World)) {
            return;
        }
        StarlightUpdateHandler.getInstance().informWorldLoad((World) iWorld);
    }

    private void onWorldUnload(WorldEvent.Unload event) {
        IWorld iWorld = event.getWorld();
        if (iWorld.isRemote() || !(iWorld instanceof World)) {
            return;
        }
        StarlightTransmissionHandler.getInstance().informWorldUnload((World) iWorld);
    }

}
