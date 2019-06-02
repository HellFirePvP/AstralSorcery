/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktPlayLiquidFountain
 * Created by HellFirePvP
 * Date: 02.06.2019 / 00:03
 */
public class PktPlayLiquidFountain extends ASPacket<PktPlayLiquidFountain> {

    private FluidStack fluidStack;
    private Vector3 pos;

    public PktPlayLiquidFountain() {}

    public PktPlayLiquidFountain(FluidStack fluidStack, Vector3 pos) {
        this.fluidStack = fluidStack;
        this.pos = pos;
    }

    @Nonnull
    @Override
    public Encoder<PktPlayLiquidFountain> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeFluidStack(buffer, packet.fluidStack);
            ByteBufUtils.writeVector(buffer, packet.pos);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktPlayLiquidFountain> decoder() {
        return buffer ->
                new PktPlayLiquidFountain(
                        ByteBufUtils.readFluidStack(buffer),
                        ByteBufUtils.readVector(buffer));
    }

    @Nonnull
    @Override
    public Handler<PktPlayLiquidFountain> handler() {
        return new Handler<PktPlayLiquidFountain>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktPlayLiquidFountain packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {

                });
            }

            @Override
            public void handle(PktPlayLiquidFountain packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
