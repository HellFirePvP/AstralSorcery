/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.packet.server;

import hellfirepvp.astralsorcery.common.data.world.GatewayCache;
import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktUpdateGateways
 * Created by HellFirePvP
 * Date: 02.06.2019 / 00:44
 */
public class PktUpdateGateways extends ASPacket<PktUpdateGateways> {

    private Map<Integer, List<GatewayCache.GatewayNode>> positions = new HashMap<>();

    public PktUpdateGateways() {}

    public PktUpdateGateways(Map<Integer, List<GatewayCache.GatewayNode>> positions) {
        this.positions = positions;
    }

    @Nonnull
    @Override
    public Encoder<PktUpdateGateways> encoder() {
        return (packet, buffer) -> {
            buffer.writeInt(packet.positions.size());
            for (Integer dim : packet.positions.keySet()) {
                List<GatewayCache.GatewayNode> gatewayNodes = packet.positions.get(dim);
                buffer.writeInt(dim);
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
    public Decoder<PktUpdateGateways> decoder() {
        return buffer -> {
            PktUpdateGateways pkt = new PktUpdateGateways();
            int dimSize = buffer.readInt();
            for (int i = 0; i < dimSize; i++) {
                int dimId = buffer.readInt();
                int nodeCount = buffer.readInt();
                for (int j = 0; j < nodeCount; j++) {
                    GatewayCache.GatewayNode newNode =
                            new GatewayCache.GatewayNode(
                                    ByteBufUtils.readPos(buffer),
                                    ByteBufUtils.readString(buffer));
                    pkt.positions.computeIfAbsent(dimId, (ii) -> new LinkedList<>()).add(newNode);
                }
            }
            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktUpdateGateways> handler() {
        return new Handler<PktUpdateGateways>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktUpdateGateways packet, NetworkEvent.Context context) {
                //TODO gateways
                //context.enqueueWork(() -> CelestialGatewaySystem.instance.updateClientCache(packet.positions));
            }

            @Override
            public void handle(PktUpdateGateways packet, NetworkEvent.Context context, LogicalSide side) {}
        };
    }
}
