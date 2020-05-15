/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.base;

import hellfirepvp.astralsorcery.common.network.PacketChannel;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ASPacket
 * Created by HellFirePvP
 * Date: 01.06.2019 / 17:57
 */
public abstract class ASPacket<T extends ASPacket<T>> {

    protected static Random rand = new Random();

    @Nonnull
    public abstract Encoder<T> encoder();

    @Nonnull
    public abstract Decoder<T> decoder();

    @Nonnull
    public abstract Handler<T> handler();

    public static interface Encoder<T extends ASPacket<T>> extends BiConsumer<T, PacketBuffer> {}

    public static interface Decoder<T extends ASPacket<T>> extends Function<PacketBuffer, T> {}

    public static interface Handler<T extends ASPacket<T>> extends BiConsumer<T, Supplier<NetworkEvent.Context>> {

        @Override
        default void accept(T t, Supplier<NetworkEvent.Context> contextSupplier) {
            NetworkEvent.Context ctx = contextSupplier.get();
            switch (ctx.getDirection().getReceptionSide()) {
                case CLIENT:
                    this.handleClient(t, ctx);
                    break;
                case SERVER:
                    this.handleServer(t, ctx);
                    break;
            }
            ctx.setPacketHandled(true);
        }

        @OnlyIn(Dist.CLIENT)
        default void handleClient(T packet, NetworkEvent.Context context) {
            this.handle(packet, context, LogicalSide.CLIENT);
        }

        default void handleServer(T packet, NetworkEvent.Context context) {
            this.handle(packet, context, LogicalSide.SERVER);
        }

        void handle(T packet, NetworkEvent.Context context, LogicalSide side);

    }

    protected final void replyWith(T packet, NetworkEvent.Context ctx) {
        PacketChannel.CHANNEL.reply(packet, ctx);
    }
}
