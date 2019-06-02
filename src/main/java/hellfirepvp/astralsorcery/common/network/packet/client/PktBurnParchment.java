/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.client;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
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

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktBurnParchment
 * Created by HellFirePvP
 * Date: 02.06.2019 / 11:39
 */
public class PktBurnParchment extends ASPacket<PktBurnParchment> {

    private DimensionType type;
    private BlockPos tablePos;

    public PktBurnParchment() {}

    public PktBurnParchment(DimensionType type, BlockPos tablePos) {
        this.tablePos = tablePos;
        this.type = type;
    }

    @Nonnull
    @Override
    public Encoder<PktBurnParchment> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeOptional(buffer, packet.type, ByteBufUtils::writeRegistryEntry);
            ByteBufUtils.writeOptional(buffer, packet.tablePos, ByteBufUtils::writePos);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktBurnParchment> decoder() {
        return buffer -> {
            PktBurnParchment pkt = new PktBurnParchment();

            pkt.type = ByteBufUtils.readOptional(buffer, ByteBufUtils::readRegistryEntry);
            pkt.tablePos = ByteBufUtils.readOptional(buffer, ByteBufUtils::readPos);

            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktBurnParchment> handler() {
        return new Handler<PktBurnParchment>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktBurnParchment packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    if(Minecraft.getInstance().currentScreen instanceof GuiMapDrawing) {
                        Minecraft.getInstance().displayGuiScreen(null);
                    }
                });
            }

            @Override
            public void handle(PktBurnParchment packet, NetworkEvent.Context context, LogicalSide side) {
                context.enqueueWork(() -> {
                    MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
                    World world = srv.getWorld(packet.type);
                    if(world != null) {
                        TileMapDrawingTable tmt = MiscUtils.getTileAt(world, packet.tablePos, TileMapDrawingTable.class, false);
                        if(tmt != null) {
                            if(tmt.burnParchment()) {
                                packet.replyWith(new PktBurnParchment(), context);
                            }
                        }
                    }
                });
            }
        };
    }
}
