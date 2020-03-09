/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import hellfirepvp.astralsorcery.common.auxiliary.charge.AlignmentChargeHandler;
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
 * Class: PktSyncCharge
 * Created by HellFirePvP
 * Date: 02.06.2019 / 00:20
 */
public class PktSyncCharge extends ASPacket<PktSyncCharge> {

    private float maxCharge = 0;
    private float charge = 0;

    public PktSyncCharge() {}

    private PktSyncCharge(float maxCharge, float charge) {
        this.maxCharge = maxCharge;
        this.charge = charge;
    }

    public PktSyncCharge(PlayerEntity player) {
        this.maxCharge = AlignmentChargeHandler.INSTANCE.getMaximumCharge(player, LogicalSide.SERVER);
        this.charge = AlignmentChargeHandler.INSTANCE.getCurrentCharge(player, LogicalSide.SERVER);
    }

    public float getMaxCharge() {
        return maxCharge;
    }

    public float getCharge() {
        return charge;
    }

    @Nonnull
    @Override
    public Encoder<PktSyncCharge> encoder() {
        return (packet, buffer) -> {
            buffer.writeFloat(packet.maxCharge);
            buffer.writeFloat(packet.charge);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktSyncCharge> decoder() {
        return buffer -> new PktSyncCharge(buffer.readFloat(), buffer.readFloat());
    }

    @Nonnull
    @Override
    public Handler<PktSyncCharge> handler() {
        return new Handler<PktSyncCharge>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktSyncCharge packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    AlignmentChargeHandler.INSTANCE.receiveCharge(packet, Minecraft.getInstance().player);
                });
            }

            @Override
            public void handle(PktSyncCharge packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
