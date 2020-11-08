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
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
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
    private RegistryKey<World> world = null;
    private BlockPos at = BlockPos.ZERO;

    public PktAttunePlayerConstellation() {}

    public PktAttunePlayerConstellation(IMajorConstellation attunement, RegistryKey<World> world, BlockPos at) {
        this.attunement = attunement;
        this.world = world;
        this.at = at;
    }

    @Nonnull
    @Override
    public Encoder<PktAttunePlayerConstellation> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeRegistryEntry(buffer, packet.attunement);
            ByteBufUtils.writeVanillaRegistryEntry(buffer, packet.world);
            ByteBufUtils.writePos(buffer, packet.at);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktAttunePlayerConstellation> decoder() {
        return buffer -> {
            PktAttunePlayerConstellation pkt = new PktAttunePlayerConstellation();

            pkt.attunement = ByteBufUtils.readRegistryEntry(buffer);
            pkt.world = ByteBufUtils.readVanillaRegistryEntry(buffer);
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
                    if (srv.forgeGetWorldMap().containsKey(packet.world)) {
                        World world = srv.getWorld(packet.world);
                        TileAttunementAltar ta = MiscUtils.getTileAt(world, packet.at, TileAttunementAltar.class, false);
                        if (ta != null && ta.getActiveRecipe() instanceof ActivePlayerAttunementRecipe) {
                            if (context.getSender().getUniqueID().equals(((ActivePlayerAttunementRecipe) ta.getActiveRecipe()).getPlayerUUID()) &&
                                    AttunePlayerRecipe.isEligablePlayer(context.getSender(), ta.getActiveConstellation())) {

                                ta.finishActiveRecipe();
                            }
                        }
                    }
                }
            });
        };
    }
}
