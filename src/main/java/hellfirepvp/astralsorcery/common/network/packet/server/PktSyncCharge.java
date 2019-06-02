/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktSyncCharge
 * Created by HellFirePvP
 * Date: 02.06.2019 / 00:20
 */
public class PktSyncCharge extends ASPacket<PktSyncCharge> {

    public float charge = 1F;

    public PktSyncCharge() {}

    public PktSyncCharge(float charge) {
        this.charge = charge;
    }

    @Nonnull
    @Override
    public Encoder<PktSyncCharge> encoder() {
        return (packet, buffer) -> buffer.writeFloat(packet.charge);
    }

    @Nonnull
    @Override
    public Decoder<PktSyncCharge> decoder() {
        return buffer -> new PktSyncCharge(buffer.readFloat());
    }

    @Nonnull
    @Override
    public Handler<PktSyncCharge> handler() {
        return new Handler<PktSyncCharge>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktSyncCharge packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {

                });
            }

            @Override
            public void handle(PktSyncCharge packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
