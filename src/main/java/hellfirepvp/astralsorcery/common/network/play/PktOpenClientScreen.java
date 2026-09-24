/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.item.TomeItem;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class PktOpenClientScreen extends PlayPacketHandler.ToClient<PktOpenClientScreen.Request> {

    static final CustomPacketPayload.Type<Request> TYPE = makeType("open_client_screen");
    static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC =
            StreamCodec.composite(CodecUtil.enumStreamCodec(ScreenType.class), Request::screenType, Request::new);

    public static final PktOpenClientScreen HANDLER = new PktOpenClientScreen();

    private PktOpenClientScreen() {
        super(TYPE);
    }

    public static Request openScreen(ScreenType screenType) {
        return new Request(screenType);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            switch (payload.screenType()) {
                case TOME -> TomeItem.openTomeScreen();
            }
        });
    }

    public record Request(ScreenType screenType) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public enum ScreenType {
        TOME
    }
}
