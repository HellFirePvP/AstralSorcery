/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.recipe.attunement.PlayerAttunementRecipe;
import hellfirepvp.astralsorcery.common.tile.TileAttunementAltar;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktAttunePlayer
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktAttunePlayer extends PlayPacketHandler.ToServer<PktAttunePlayer.Request> {

    static final CustomPacketPayload.Type<PktAttunePlayer.Request> TYPE = makeType("attune_player");
    static final StreamCodec<RegistryFriendlyByteBuf, PktAttunePlayer.Request> CODEC =
            StreamCodec.composite(BlockPos.STREAM_CODEC, Request::altarPos, Request::new);

    public static final PktAttunePlayer HANDLER = new PktAttunePlayer();

    private PktAttunePlayer() {
        super(TYPE);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer sPlayer) {
                MiscUtil.getTileAt(sPlayer.serverLevel(), payload.altarPos(), TileAttunementAltar.class, false).ifPresent(altar -> {
                    altar.getTileData().getActiveRecipe().ifPresent(activeRecipe -> {
                        if (activeRecipe instanceof PlayerAttunementRecipe.Active playerAttunement &&
                                playerAttunement.getPlayerUUID().equals(sPlayer.getUUID()) &&
                                playerAttunement.matches(altar)) {
                            altar.finishActiveRecipe();
                        }
                    });
                });
            }
        });
    }

    public static record Request(BlockPos altarPos) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
