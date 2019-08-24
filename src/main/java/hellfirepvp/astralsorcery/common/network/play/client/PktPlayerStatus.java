/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktPlayerStatus
 * Created by HellFirePvP
 * Date: 02.06.2019 / 14:01
 */
public class PktPlayerStatus extends ASPacket<PktPlayerStatus> {

    private UUID playerUUID = null;
    private boolean status = false;

    public PktPlayerStatus() {}

    public PktPlayerStatus(UUID playerUUID, boolean active) {
        this.playerUUID = playerUUID;
        this.status = active;
    }

    @Nonnull
    @Override
    public Encoder<PktPlayerStatus> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeUUID(buffer, packet.playerUUID);
            buffer.writeBoolean(packet.status);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktPlayerStatus> decoder() {
        return buffer -> {
            PktPlayerStatus pkt = new PktPlayerStatus();

            pkt.playerUUID = ByteBufUtils.readUUID(buffer);
            pkt.status = buffer.readBoolean();

            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktPlayerStatus> handler() {
        return (packet, context, side) -> {
            context.enqueueWork(() -> {
                //TODO anti afk cheese
                //PlayerActivityManager.INSTANCE.setStatusServer(packet.playerUUID, packet.status);
            });
        };
    }
}
