/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.component.AstrolabeAngleComponent;
import hellfirepvp.astralsorcery.common.item.AstrolabeItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.ItemsAS;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktAdjustAstrolabeAngle
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktAdjustAstrolabeAngle extends PlayPacketHandler.ToServer<PktAdjustAstrolabeAngle.Request> {

    static final CustomPacketPayload.Type<Request> TYPE = makeType("adjust_astrolabe_angle");
    static final StreamCodec<RegistryFriendlyByteBuf, PktAdjustAstrolabeAngle.Request> CODEC =
            StreamCodec.composite(ByteBufCodecs.FLOAT, Request::newAngle, Request::new);

    public static final PktAdjustAstrolabeAngle HANDLER = new PktAdjustAstrolabeAngle();

    private PktAdjustAstrolabeAngle() {
        super(TYPE);
    }

    public static Request adjustAngle(float newAngle) {
        return new Request(newAngle);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sPlayer && AstrolabeItem.isUsingAstrolabe(sPlayer)) {
                ItemStack useStack = sPlayer.getItemInHand(sPlayer.getUsedItemHand());
                if (useStack.is(ItemsAS.ASTROLABE)) {
                    AstrolabeAngleComponent existing = useStack.getOrDefault(DataComponentsAS.ASTROLABE_ANGLE, AstrolabeAngleComponent.DEFAULT);
                    useStack.set(DataComponentsAS.ASTROLABE_ANGLE, new AstrolabeAngleComponent(payload.newAngle(), existing.matchesAll()));
                }
            }
        });
    }

    public static record Request(float newAngle) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

}
