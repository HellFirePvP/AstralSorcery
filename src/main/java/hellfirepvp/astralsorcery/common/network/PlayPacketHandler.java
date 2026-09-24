/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.util.NameUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PlayPacketHandler
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public abstract class PlayPacketHandler<T extends CustomPacketPayload> {

    private final CustomPacketPayload.Type<T> type;

    PlayPacketHandler(CustomPacketPayload.Type<T> type) {
        this.type = type;
    }

    public final CustomPacketPayload.Type<T> type() {
        return this.type;
    }

    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> makeType(String name) {
        return new CustomPacketPayload.Type<>(NameUtil.prefixPath(AstralSorcery.key(name), "play/"));
    }

    public abstract StreamCodec<RegistryFriendlyByteBuf, T> codec();

    public abstract void register(PayloadRegistrar registrar);

    public abstract static class ToClient<T extends CustomPacketPayload> extends PlayPacketHandler<T> implements IPayloadHandler<T> {

        protected ToClient(CustomPacketPayload.Type<T> type) {
            super(type);
        }

        public final void register(PayloadRegistrar registrar) {
            registrar.playToClient(this.type(), this.codec(), this);
        }
    }

    public abstract static class ToServer<T extends CustomPacketPayload> extends PlayPacketHandler<T> implements IPayloadHandler<T> {

        protected ToServer(CustomPacketPayload.Type<T> type) {
            super(type);
        }

        public final void register(PayloadRegistrar registrar) {
            registrar.playToServer(this.type(), this.codec(), this);
        }
    }

    public abstract static class BiDirectional<T extends CustomPacketPayload> extends PlayPacketHandler<T> {

        protected BiDirectional(CustomPacketPayload.Type<T> type) {
            super(type);
        }

        public final void register(PayloadRegistrar registrar) {
            registrar.playBidirectional(this.type(), this.codec(), new DirectionalPayloadHandler<>(this::handleClient, this::handleServer));
        }

        public abstract void handleClient(T payload, IPayloadContext context);

        public abstract void handleServer(T payload, IPayloadContext context);
    }

}
