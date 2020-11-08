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
import net.minecraft.item.DyeColor;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktLoginSyncGateway
 * Created by HellFirePvP
 * Date: 24.08.2019 / 00:17
 */
public class PktLoginSyncGateway extends ASLoginPacket<PktLoginSyncGateway> {

    private Map<RegistryKey<World>, Collection<GatewayCache.GatewayNode>> positions = new HashMap<>();

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
            for (RegistryKey<World> dim : packet.positions.keySet()) {
                ByteBufUtils.writeVanillaRegistryEntry(buffer, dim);
                ByteBufUtils.writeCollection(buffer, packet.positions.get(dim), (buf, node) -> node.write(buf));
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
                RegistryKey<World> dim = ByteBufUtils.readVanillaRegistryEntry(buffer);
                pkt.positions.put(dim, ByteBufUtils.readList(buffer, GatewayCache.GatewayNode::read));
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
