/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.common.constellation.IMajorConstellation;
import hellfirepvp.astralsorcery.common.crafting.nojson.attunement.AttunePlayerRecipe;
import hellfirepvp.astralsorcery.common.crafting.nojson.attunement.active.ActivePlayerAttunementRecipe;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktAttuneConstellation
 * Created by HellFirePvP
 * Date: 02.06.2019 / 11:35
 */
public class PktAttunePlayerConstellation extends ASPacket<PktAttunePlayerConstellation> {

    private IMajorConstellation attunement = null;
    private DimensionType type = null;
    private BlockPos at = BlockPos.ZERO;

    public PktAttunePlayerConstellation() {}

    public PktAttunePlayerConstellation(IMajorConstellation attunement, DimensionType type, BlockPos at) {
        this.attunement = attunement;
        this.type = type;
        this.at = at;
    }

    @Nonnull
    @Override
    public Encoder<PktAttunePlayerConstellation> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeRegistryEntry(buffer, packet.attunement);
            ByteBufUtils.writeRegistryEntry(buffer, packet.type);
            ByteBufUtils.writePos(buffer, packet.at);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktAttunePlayerConstellation> decoder() {
        return buffer -> {
            PktAttunePlayerConstellation pkt = new PktAttunePlayerConstellation();

            pkt.attunement = ByteBufUtils.readRegistryEntry(buffer);
            pkt.type = ByteBufUtils.readRegistryEntry(buffer);
            pkt.at = ByteBufUtils.readPos(buffer);

            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktAttunePlayerConstellation> handler() {
        return (packet, context, side) -> {
            context.enqueueWork(() -> {
                IMajorConstellation cst = packet.attunement;
                if (cst != null) {
                    MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
                    World world = srv.getWorld(packet.type);
                    TileAttunementAltar ta = MiscUtils.getTileAt(world, packet.at, TileAttunementAltar.class, false);
                    if (ta != null && ta.getActiveRecipe() instanceof ActivePlayerAttunementRecipe) {
                        if (context.getSender().getUniqueID().equals(((ActivePlayerAttunementRecipe) ta.getActiveRecipe()).getPlayerUUID()) &&
                                AttunePlayerRecipe.isEligablePlayer(context.getSender(), ta.getActiveConstellation())) {

                            ta.finishActiveRecipe();
                        }
                    }
                }
            });
        };
    }
}
