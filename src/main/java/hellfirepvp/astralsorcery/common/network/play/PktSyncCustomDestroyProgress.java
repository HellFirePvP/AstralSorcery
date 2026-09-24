/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.client.helper.ClientBlockDestroyProgressHelper;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktSyncCustomDestroyProgress
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktSyncCustomDestroyProgress extends PlayPacketHandler.ToClient<PktSyncCustomDestroyProgress.Request> {

    static final CustomPacketPayload.Type<Request> TYPE = makeType("sync_custom_destroy_progress");
    static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            Request::entityId,
            BlockPos.STREAM_CODEC,
            Request::originPos,
            BlockPos.STREAM_CODEC,
            Request::breakPos,
            ByteBufCodecs.INT,
            Request::progress,
            Request::new
    );

    public static final PktSyncCustomDestroyProgress HANDLER = new PktSyncCustomDestroyProgress();

    private PktSyncCustomDestroyProgress() {
        super(TYPE);
    }

    public static Request destroyProgress(int entityId, BlockPos originPos, BlockPos breakPos, int progress) {
        return new Request(entityId, originPos, breakPos, progress);
    }

    public static Request resetProgress(int entityId, BlockPos originPos) {
        return new Request(entityId, originPos, originPos, -1);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (payload.progress >= 0 && payload.progress < 10) {
                ClientBlockDestroyProgressHelper.addBreakProgress(payload.entityId, payload.originPos, payload.breakPos, payload.progress);
            } else {
                ClientBlockDestroyProgressHelper.removeBreakProgress(payload.entityId, payload.originPos);
            }
        });
    }

    public record Request(int entityId, BlockPos originPos, BlockPos breakPos, int progress) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
