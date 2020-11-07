/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.common.network.PacketChannel;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.network.play.server.PktPlayEffect;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.PlayerReference;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRevokeGatewayAccess
 * Created by HellFirePvP
 * Date: 21.10.2020 / 22:20
 */
public class PktRevokeGatewayAccess extends ASPacket<PktRevokeGatewayAccess> {

    private DimensionType type = null;
    private BlockPos pos = BlockPos.ZERO;
    private UUID revokeUUID = null;

    public PktRevokeGatewayAccess() {}

    public PktRevokeGatewayAccess(DimensionType type, BlockPos pos, UUID revokeUUID) {
        this.type = type;
        this.pos = pos;
        this.revokeUUID = revokeUUID;
    }

    @Nonnull
    @Override
    public Encoder<PktRevokeGatewayAccess> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeRegistryEntry(buffer, packet.type);
            ByteBufUtils.writePos(buffer, packet.pos);
            ByteBufUtils.writeUUID(buffer, packet.revokeUUID);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktRevokeGatewayAccess> decoder() {
        return buffer -> new PktRevokeGatewayAccess(
                ByteBufUtils.readRegistryEntry(buffer),
                ByteBufUtils.readPos(buffer),
                ByteBufUtils.readUUID(buffer));
    }

    @Nonnull
    @Override
    public Handler<PktRevokeGatewayAccess> handler() {
        return (packet, context, side) -> {
            if (side.isServer()) {
                PlayerEntity sender = context.getSender();
                if (sender == null) {
                    return;
                }

                MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
                World world = srv.getWorld(packet.type);

                TileCelestialGateway gateway = MiscUtils.getTileAt(world, packet.pos, TileCelestialGateway.class, false);
                if (gateway != null && gateway.isLocked() && gateway.getOwner() != null && gateway.getOwner().isPlayer(sender)) {
                    BlockPos testPos = Vector3.atEntityCorner(sender).toBlockPos();
                    TileCelestialGateway playerGateway = MiscUtils.getTileAt(world, testPos, TileCelestialGateway.class, false);
                    if (gateway.equals(playerGateway)) {
                        PlayerReference removedPlayer = gateway.removeAllowedUser(packet.revokeUUID);
                        if (removedPlayer != null) {
                            PktPlayEffect pkt = new PktPlayEffect(PktPlayEffect.Type.GATEWAY_REVOKE_EFFECT)
                                    .addData(buffer -> ByteBufUtils.writePos(buffer, gateway.getPos()));
                            PacketChannel.CHANNEL.sendToPlayer(sender, pkt);

                            ITextComponent accessGrantedMessage = new TranslationTextComponent(
                                    "astralsorcery.misc.link.gateway.unlink",
                                    removedPlayer.getPlayerName())
                                    .applyTextStyle(TextFormatting.GREEN);
                            sender.sendMessage(accessGrantedMessage);
                        }
                    }
                }
            }
        };
    }
}
