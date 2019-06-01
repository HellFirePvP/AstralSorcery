/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.base;

import hellfirepvp.astralsorcery.common.network.PacketChannel;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

import javax.annotation.Nonnull;
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
            this.handle(t, ctx, ctx.getDirection().getReceptionSide());
        }

        void handle(T packet, NetworkEvent.Context context, LogicalSide recipientSide);

    }

    protected final void replyWith(T packet, NetworkEvent.Context ctx) {
        PacketChannel.CHANNEL.reply(packet, ctx);
    }

}
