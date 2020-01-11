/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncStepAssist
 * Created by HellFirePvP
 * Date: 02.06.2019 / 00:32
 */
public class PktSyncStepAssist extends ASPacket<PktSyncStepAssist> {

    private float stepHeight;

    public PktSyncStepAssist() {}

    public PktSyncStepAssist(float stepHeight) {
        this.stepHeight = stepHeight - 0.4F;
    }

    @Nonnull
    @Override
    public Encoder<PktSyncStepAssist> encoder() {
        return (packet, buffer) -> buffer.writeFloat(packet.stepHeight);
    }

    @Nonnull
    @Override
    public Decoder<PktSyncStepAssist> decoder() {
        return buffer -> {
            PktSyncStepAssist pkt = new PktSyncStepAssist();
            pkt.stepHeight = buffer.readFloat();
            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktSyncStepAssist> handler() {
        return new Handler<PktSyncStepAssist>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktSyncStepAssist packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    PlayerEntity player = Minecraft.getInstance().player;
                    if (player != null) {
                        player.stepHeight = packet.stepHeight;
                    }
                });
            }

            @Override
            public void handle(PktSyncStepAssist packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
