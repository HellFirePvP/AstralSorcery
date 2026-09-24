/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.linking.session.LinkSessionHelper;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktRequestCancelLinkSession
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktRequestCancelLinkSession extends PlayPacketHandler.ToServer<PktRequestCancelLinkSession.Request> {

    static final CustomPacketPayload.Type<Request> TYPE = makeType("request_cancel_link_session");
    static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.unit(Request.INSTANCE);

    public static final PktRequestCancelLinkSession HANDLER = new PktRequestCancelLinkSession();

    private PktRequestCancelLinkSession() {
        super(TYPE);
    }

    public static Request cancelSession() {
        return Request.INSTANCE;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sPlayer) {
                LinkSessionHelper.stopSession(sPlayer);
            }
        });
    }

    public static class Request implements CustomPacketPayload {

        private static final Request INSTANCE = new Request();

        private Request() {}

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
