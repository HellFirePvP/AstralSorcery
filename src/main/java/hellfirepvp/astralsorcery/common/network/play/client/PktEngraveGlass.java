/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.common.constellation.DrawnConstellation;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.tile.TileRefractionTable;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktEngraveGlass
 * Created by HellFirePvP
 * Date: 02.06.2019 / 13:48
 */
public class PktEngraveGlass extends ASPacket<PktEngraveGlass> {

    private DimensionType type;
    private BlockPos pos;
    private List<DrawnConstellation> constellations = new LinkedList<>();

    public PktEngraveGlass() {}

    public PktEngraveGlass(DimensionType type, BlockPos pos, List<DrawnConstellation> constellations) {
        this.type = type;
        this.pos = pos;
        this.constellations = constellations;
    }
    @Nonnull
    @Override
    public Encoder<PktEngraveGlass> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeRegistryEntry(buffer, packet.type);
            ByteBufUtils.writePos(buffer, packet.pos);
            ByteBufUtils.writeList(buffer, packet.constellations, (buf, cst) -> {
                buf.writeInt(cst.getPoint().x);
                buf.writeInt(cst.getPoint().y);
                ByteBufUtils.writeRegistryEntry(buf, cst.getConstellation());
            });
        };
    }

    @Nonnull
    @Override
    public Decoder<PktEngraveGlass> decoder() {
        return buffer -> {
            PktEngraveGlass pkt = new PktEngraveGlass();

            pkt.type = ByteBufUtils.readRegistryEntry(buffer);
            pkt.pos = ByteBufUtils.readPos(buffer);
            pkt.constellations = ByteBufUtils.readList(buffer, buf ->
                    new DrawnConstellation(
                            new Point(buf.readInt(), buf.readInt()),
                            ByteBufUtils.readRegistryEntry(buf)));

            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktEngraveGlass> handler() {
        return (packet, context, side) -> {
            context.enqueueWork(() -> {
                MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
                World world = srv.getWorld(packet.type);
                TileRefractionTable tmt = MiscUtils.getTileAt(world, packet.pos, TileRefractionTable.class, false);
                if (tmt != null && !packet.constellations.isEmpty()) {
                    List<DrawnConstellation> cstList = packet.constellations.subList(0, Math.min(3, packet.constellations.size()));
                    tmt.engraveGlass(cstList);
                }
            });
        };
    }
}
