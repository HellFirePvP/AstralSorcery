/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.client.helper.ClientLinkHelper;
import hellfirepvp.astralsorcery.common.linking.session.ActiveLinkSession;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktUpdateLinkSession
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktUpdateLinkSession extends PlayPacketHandler.ToClient<PktUpdateLinkSession.Request> {

    static final CustomPacketPayload.Type<Request> TYPE = makeType("update_link_session");
    static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.fromCodec(ActiveLinkSession.CODEC)),
            Request::session,
            Request::new
    );

    public static final PktUpdateLinkSession HANDLER = new PktUpdateLinkSession();

    private PktUpdateLinkSession() {
        super(TYPE);
    }

    public static Request updateSession(ActiveLinkSession session) {
        return new Request(Optional.of(session));
    }

    public static Request stopSession() {
        return new Request(Optional.empty());
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> this.handleClient(payload));
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient(Request payload) {
        ClientLinkHelper.setActiveSession(payload.session().orElse(null));
    }

    public static record Request(Optional<ActiveLinkSession> session) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

}
