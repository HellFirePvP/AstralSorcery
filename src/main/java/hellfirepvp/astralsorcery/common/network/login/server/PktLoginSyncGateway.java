/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.login.server;

import hellfirepvp.astralsorcery.common.auxiliary.gateway.CelestialGatewayHandler;
import hellfirepvp.astralsorcery.common.data.world.GatewayCache;
import hellfirepvp.astralsorcery.common.network.base.ASLoginPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktLoginSyncGateway
 * Created by HellFirePvP
 * Date: 24.08.2019 / 00:17
 */
public class PktLoginSyncGateway extends ASLoginPacket<PktLoginSyncGateway> {

    private Map<ResourceLocation, List<GatewayCache.GatewayNode>> positions = new HashMap<>();

    public PktLoginSyncGateway() {}

    public static PktLoginSyncGateway makeLogin() {
        PktLoginSyncGateway pkt = new PktLoginSyncGateway();
        pkt.positions = CelestialGatewayHandler.INSTANCE.getGatewayCache(LogicalSide.SERVER);
        return pkt;
    }

    @Nonnull
    @Override
    public Encoder<PktLoginSyncGateway> encoder() {
        return (packet, buffer) -> {
            buffer.writeInt(packet.positions.size());
            for (ResourceLocation dim : packet.positions.keySet()) {
                List<GatewayCache.GatewayNode> gatewayNodes = packet.positions.get(dim);
                ByteBufUtils.writeResourceLocation(buffer, dim);
                buffer.writeInt(gatewayNodes.size());
                for (GatewayCache.GatewayNode node : gatewayNodes) {
                    ByteBufUtils.writePos(buffer, node);
                    ByteBufUtils.writeString(buffer, node.getDisplayName());
                }
            }
        };
    }

    @Nonnull
    @Override
    public Decoder<PktLoginSyncGateway> decoder() {
        return buffer -> {
            PktLoginSyncGateway pkt = new PktLoginSyncGateway();
            int dimSize = buffer.readInt();
            for (int i = 0; i < dimSize; i++) {
                ResourceLocation dim = ByteBufUtils.readResourceLocation(buffer);
                int nodeCount = buffer.readInt();
                for (int j = 0; j < nodeCount; j++) {
                    GatewayCache.GatewayNode newNode =
                            new GatewayCache.GatewayNode(
                                    ByteBufUtils.readPos(buffer),
                                    ByteBufUtils.readString(buffer));
                    pkt.positions.computeIfAbsent(dim, (ii) -> new LinkedList<>()).add(newNode);
                }
            }
            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktLoginSyncGateway> handler() {
        return new Handler<PktLoginSyncGateway>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktLoginSyncGateway packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> {
                    CelestialGatewayHandler.INSTANCE.updateClientCache(packet.positions);
                    acknowledge(context);
                });
            }

            @Override
            public void handle(PktLoginSyncGateway packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
