/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.channel;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkInstance;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleSendChannel
 * Created by HellFirePvP
 * Date: 30.05.2019 / 17:56
 */
public abstract class SimpleSendChannel extends SimpleChannel {

    public SimpleSendChannel(NetworkInstance instance) {
        super(instance);
    }

    public <P> void sendToPlayer(EntityPlayer player, P packet) {
        if (player instanceof EntityPlayerMP) {
            this.send(PacketDistributor.PLAYER.with(() -> (EntityPlayerMP) player), packet);
        }
    }

    public <P> void sendToAll(P packet) {
        this.send(PacketDistributor.ALL.noArg(), packet);
    }

    public <P> void sendToAllObservingChunk(P packet, Chunk ch) {
        this.send(PacketDistributor.TRACKING_CHUNK.with(() -> ch), packet);
    }

    public <P> void sendToAllAround(P packet, PacketDistributor.TargetPoint point) {
        this.send(PacketDistributor.NEAR.with(() -> point), packet);
    }

}
