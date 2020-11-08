/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play.client;

import hellfirepvp.astralsorcery.common.network.base.ASPacket;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.data.ByteBufUtils;
import hellfirepvp.astralsorcery.common.util.world.WorldSeedCache;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PktRequestSeed
 * Created by HellFirePvP
 * Date: 02.06.2019 / 14:13
 */
public class PktRequestSeed extends ASPacket<PktRequestSeed> {

    private RegistryKey<World> dim;
    private Integer session;
    private Long seed;

    public PktRequestSeed() {}

    public PktRequestSeed(Integer session, RegistryKey<World> dim) {
        this.dim = dim;
        this.session = session;
        this.seed = -1L;
    }

    private PktRequestSeed seed(Long seed) {
        this.seed = seed;
        return this;
    }

    @Nonnull
    @Override
    public Encoder<PktRequestSeed> encoder() {
        return (packet, buffer) -> {
            ByteBufUtils.writeOptional(buffer, packet.dim, ByteBufUtils::writeVanillaRegistryEntry);
            ByteBufUtils.writeOptional(buffer, packet.session, PacketBuffer::writeInt);
            ByteBufUtils.writeOptional(buffer, packet.seed, PacketBuffer::writeLong);
        };
    }

    @Nonnull
    @Override
    public Decoder<PktRequestSeed> decoder() {
        return buffer -> {
            PktRequestSeed pkt = new PktRequestSeed();

            pkt.dim = ByteBufUtils.readOptional(buffer, ByteBufUtils::readVanillaRegistryEntry);
            pkt.session = ByteBufUtils.readOptional(buffer, PacketBuffer::readInt);
            pkt.seed = ByteBufUtils.readOptional(buffer, PacketBuffer::readLong);

            return pkt;
        };
    }

    @Nonnull
    @Override
    public Handler<PktRequestSeed> handler() {
        return new Handler<PktRequestSeed>() {
            @Override
            @OnlyIn(Dist.CLIENT)
            public void handleClient(PktRequestSeed packet, NetworkEvent.Context context) {
                context.enqueueWork(() -> WorldSeedCache.updateSeedCache(packet.dim, packet.session, packet.seed));
            }

            @Override
            public void handle(PktRequestSeed packet, NetworkEvent.Context context, LogicalSide side) {
                context.enqueueWork(() -> {
                    //TODO 1.16.2 re-check once worlds are not all constantly loaded
                    MinecraftServer srv = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
                    ServerWorld w = srv.getWorld(packet.dim);
                    if (w != null) {
                        PktRequestSeed seedResponse = new PktRequestSeed(packet.session, packet.dim);
                        seedResponse.seed(MiscUtils.getRandomWorldSeed(w));
                        packet.replyWith(seedResponse, context);
                    }
                });
            }
        };
    }
}
