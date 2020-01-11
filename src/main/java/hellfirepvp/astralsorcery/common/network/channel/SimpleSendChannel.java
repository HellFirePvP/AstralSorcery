/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.channel;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.NetworkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleSendChannel
 * Created by HellFirePvP
 * Date: 30.05.2019 / 17:56
 */
public abstract class SimpleSendChannel {

    private final SimpleChannel channel;

    public SimpleSendChannel(SimpleChannel channel) {
        this.channel = channel;
    }

    public <P extends ASPacket<P>> void sendToPlayer(PlayerEntity player, P packet) {
        if (player instanceof ServerPlayerEntity) {
            this.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) player), packet);
        }
    }

    public <P extends ASPacket<P>> void sendToAll(P packet) {
        this.send(PacketDistributor.ALL.noArg(), packet);
    }

    public <P extends ASPacket<P>> void sendToAllObservingChunk(P packet, Chunk ch) {
        this.send(PacketDistributor.TRACKING_CHUNK.with(() -> ch), packet);
    }

    public <P extends ASPacket<P>> void sendToAllAround(P packet, PacketDistributor.TargetPoint point) {
        this.send(PacketDistributor.NEAR.with(() -> point), packet);
    }

    public <MSG> void sendToServer(MSG message) {
        channel.sendToServer(message);
    }

    public <MSG> void sendTo(MSG message, NetworkManager manager, NetworkDirection direction) {
        channel.sendTo(message, manager, direction);
    }

    public <MSG> void send(PacketDistributor.PacketTarget target, MSG message) {
        channel.send(target, message);
    }

    public <MSG> void reply(MSG msgToReply, NetworkEvent.Context context) {
        channel.reply(msgToReply, context);
    }

    public <MSG> SimpleChannel.MessageBuilder<MSG> messageBuilder(final Class<MSG> type, int id) {
        return channel.messageBuilder(type, id);
    }

}
