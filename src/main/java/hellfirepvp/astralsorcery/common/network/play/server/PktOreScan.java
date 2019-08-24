/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.server;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktOreScan
 * Created by HellFirePvP
 * Date: 01.06.2019 / 23:55
 */
public class PktOreScan extends ASPacket<PktOreScan> {

    private List<BlockPos> positions = new ArrayList<>();
    private boolean tumble = false;

    public PktOreScan() {}

    public PktOreScan(List<BlockPos> positions, boolean tumble) {
        this.positions = positions;
        this.tumble = tumble;
    }

    @Nonnull
    @Override
    public Encoder<PktOreScan> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeList(buffer, packet.positions, ByteBufUtils::writePos);
            buffer.writeBoolean(packet.tumble);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktOreScan> decoder() {
        return buffer ->
                new PktOreScan(
                        ByteBufUtils.readList(buffer, ByteBufUtils::readPos),
                        buffer.readBoolean());
    }

    @Nonnull
    @Override
    public Handler<PktOreScan> handler() {
        return new Handler<PktOreScan>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktOreScan packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {

                });
            }

            @Override
            public void handle(PktOreScan packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
