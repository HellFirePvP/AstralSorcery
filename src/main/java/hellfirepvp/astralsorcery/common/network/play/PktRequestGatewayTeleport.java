/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.lib.DataAS;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.tile.TileCelestialGateway;
import hellfirepvp.astralsorcery.common.util.EntityUtil;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktRequestGatewayTeleport
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktRequestGatewayTeleport extends PlayPacketHandler.ToServer<PktRequestGatewayTeleport.Request> {

    static final CustomPacketPayload.Type<Request> TYPE = makeType("request_gateway_teleport");
    static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION),
            Request::levelKey,
            BlockPos.STREAM_CODEC,
            Request::targetPos,
            Request::new
    );

    public static final PktRequestGatewayTeleport HANDLER = new PktRequestGatewayTeleport();

    private PktRequestGatewayTeleport() {
        super(TYPE);
    }

    public static Request teleportTo(ResourceKey<Level> levelKey, BlockPos targetPos) {
        return new Request(levelKey, targetPos);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sPlayer) {
                MinecraftServer srv = sPlayer.getServer();
                if (srv == null) return;
                MiscUtil.getTileAt(sPlayer.level(), sPlayer.blockPosition(), TileCelestialGateway.class, true).ifPresent(thisGateway -> {
                    if (!thisGateway.hasStructure() || !thisGateway.doesSeeSky()) {
                        return;
                    }

                    ServerLevel sLevel = srv.getLevel(payload.levelKey());
                    if (sLevel != null) {
                        DataAS.DOMAIN_AS.getData(sLevel, DataAS.KEY_CELESTIAL_GATEWAY_DATA).getGateway(payload.targetPos()).ifPresent(targetEntry ->{
                            MiscUtil.getTileAt(sLevel, targetEntry.getPos(), TileCelestialGateway.class, true).ifPresent(targetGateway -> {
                                if (!targetGateway.hasStructure() || !targetGateway.doesSeeSky()) {
                                    return;
                                }
                                EntityUtil.transferEntity(sPlayer, sLevel.dimension(), Vector3.atCenter(targetGateway.getBlockPos()));
                            });
                        });
                    }
                });
            }
        });
    }

    public record Request(ResourceKey<Level> levelKey, BlockPos targetPos) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
