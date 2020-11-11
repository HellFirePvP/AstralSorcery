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
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
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

    private RegistryKey<World> dim;
    private BlockPos pos;
    private List<DrawnConstellation> constellations = new LinkedList<>();

    public PktEngraveGlass() {}

    public PktEngraveGlass(RegistryKey<World> dim, BlockPos pos, List<DrawnConstellation> constellations) {
        this.dim = dim;
        this.pos = pos;
        this.constellations = constellations;
    }
    @Nonnull
    @Override
    public Encoder<PktEngraveGlass> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeVanillaRegistryEntry(buffer, packet.dim);
            ByteBufUtils.writePos(buffer, packet.pos);
            ByteBufUtils.writeCollection(buffer, packet.constellations, (buf, cst) -> {
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

            pkt.dim = ByteBufUtils.readVanillaRegistryEntry(buffer);
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
                //TODO 1.16.2 re-check once worlds are not all constantly loaded
                MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
                World world = srv.getWorld(packet.dim);
                TileRefractionTable tmt = MiscUtils.getTileAt(world, packet.pos, TileRefractionTable.class, false);
                if (tmt != null && !packet.constellations.isEmpty()) {
                    List<DrawnConstellation> cstList = packet.constellations.subList(0, Math.min(3, packet.constellations.size()));
                    tmt.engraveGlass(cstList);
                }
            });
        };
    }
}
