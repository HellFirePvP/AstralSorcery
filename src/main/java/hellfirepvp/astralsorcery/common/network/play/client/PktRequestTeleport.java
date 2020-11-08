/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.data.world.GatewayCache;
import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRequestTeleport
 * Created by HellFirePvP
 * Date: 02.06.2019 / 14:53
 */
public class PktRequestTeleport extends ASPacket<PktRequestTeleport> {

    private RegistryKey<World> dim;
    private BlockPos pos;

    public PktRequestTeleport() {}

    public PktRequestTeleport(RegistryKey<World> dim, BlockPos pos) {
        this.dim = dim;
        this.pos = pos;
    }
    @Nonnull
    @Override
    public Encoder<PktRequestTeleport> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeVanillaRegistryEntry(buffer, packet.dim);
            ByteBufUtils.writePos(buffer, packet.pos);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktRequestTeleport> decoder() {
        return buffer -> {
            PktRequestTeleport pkt = new PktRequestTeleport();

            pkt.dim = ByteBufUtils.readVanillaRegistryEntry(buffer);
            pkt.pos = ByteBufUtils.readPos(buffer);

            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktRequestTeleport> handler() {
        return (packet, context, side) -> {
            context.enqueueWork(() -> {
                //TODO 1.16.2 re-check once worlds are not all constantly loaded
                PlayerEntity player = context.getSender();
                TileCelestialGateway gate = MiscUtils.getTileAt(player.world, Vector3.atEntityCorner(player).toBlockPos(), TileCelestialGateway.class, false);
                if (gate != null && gate.hasMultiblock() && gate.doesSeeSky()) {
                    MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
                    if (server != null) {
                        World to = server.getWorld(packet.dim);
                        if (to != null) {
                            GatewayCache.GatewayNode node = DataAS.DOMAIN_AS.getData(to, DataAS.KEY_GATEWAY_CACHE).getGatewayNode(packet.pos);
                            if (node != null && node.hasAccess(player)) {
                                AstralSorcery.getProxy().scheduleDelayed(() -> MiscUtils.transferEntityTo(player, to.getDimensionKey(), packet.pos));
                            }
                        }
                    }
                }
            });
        };
    }
}
