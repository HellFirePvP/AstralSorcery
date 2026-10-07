/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.tile.TileStructureMarker;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktSetStructureMarker
 * Created by HellFirePvP
 * Date: 07.10.2026 / 10:27
 */
public class PktSetStructureMarker extends PlayPacketHandler.ToServer<PktSetStructureMarker.Request> {

    static final CustomPacketPayload.Type<Request> TYPE = makeType("set_structure_marker");
    static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, Request::pos,
            ResourceLocation.STREAM_CODEC, Request::markerId,
            Request::new);

    public static final PktSetStructureMarker HANDLER = new PktSetStructureMarker();

    private PktSetStructureMarker() {
        super(TYPE);
    }

    public static Request setMarker(BlockPos pos, ResourceLocation markerId) {
        return new Request(pos, markerId);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer sPlayer) || !sPlayer.canUseGameMasterBlocks()) {
                return;
            }
            MiscUtil.getTileAt(sPlayer.level(), payload.pos(), TileStructureMarker.class, false)
                    .ifPresent(marker -> {
                        marker.getTileData().setMarkerId(payload.markerId());
                        marker.markForUpdate();
                    });
        });
    }

    public record Request(BlockPos pos, ResourceLocation markerId) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
