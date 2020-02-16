/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.client.screen.ScreenTelescope;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.tile.TileTelescope;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRotateTelescope
 * Created by HellFirePvP
 * Date: 02.06.2019 / 14:58
 */
public class PktRotateTelescope extends ASPacket<PktRotateTelescope> {

    private boolean isClockwise = false;
    private DimensionType type = null;
    private BlockPos pos = BlockPos.ZERO;

    public PktRotateTelescope() {}

    public PktRotateTelescope(boolean isClockwise, DimensionType type, BlockPos pos) {
        this.isClockwise = isClockwise;
        this.type = type;
        this.pos = pos;
    }
    @Nonnull
    @Override
    public Encoder<PktRotateTelescope> encoder() {
        return (packet, buffer) -> {
            buffer.writeBoolean(packet.isClockwise);
            ByteBufUtils.writeOptional(buffer, packet.type, ByteBufUtils::writeRegistryEntry);
            ByteBufUtils.writePos(buffer, packet.pos);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktRotateTelescope> decoder() {
        return buffer -> {
            PktRotateTelescope pkt = new PktRotateTelescope();

            pkt.isClockwise = buffer.readBoolean();
            pkt.type = ByteBufUtils.readOptional(buffer, ByteBufUtils::readRegistryEntry);
            pkt.pos = ByteBufUtils.readPos(buffer);

            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktRotateTelescope> handler() {
        return new Handler<PktRotateTelescope>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktRotateTelescope packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    Optional<World> clWorld = LogicalSidedProvider.CLIENTWORLD.get(LogicalSide.CLIENT);
                    clWorld.ifPresent(world -> {
                        TileTelescope tt = MiscUtils.getTileAt(world, packet.pos, TileTelescope.class, false);
                        if(tt != null) {
                            tt.setRotation(packet.isClockwise ? tt.getRotation().nextClockWise() : tt.getRotation().nextCounterClockWise());
                        }
                    });
                    if (Minecraft.getInstance().currentScreen instanceof ScreenTelescope) {
                        ((ScreenTelescope) Minecraft.getInstance().currentScreen).handleRotationChange(packet.isClockwise);
                    }
                });
            }

            @Override
            public void handle(PktRotateTelescope packet, NetworkEvent.Context context, LogicalSide side) {
                context.enqueueWork(() -> {
                    MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
                    World world = srv.getWorld(packet.type);

                    TileTelescope tt = MiscUtils.getTileAt(world, packet.pos, TileTelescope.class, false);
                    if(tt != null) {
                        tt.setRotation(packet.isClockwise ? tt.getRotation().nextClockWise() : tt.getRotation().nextCounterClockWise());
                        packet.replyWith(new PktRotateTelescope(packet.isClockwise, packet.type, packet.pos), context);
                    }
                });
            }
        };
    }
}
