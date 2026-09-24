/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.research.ResearchManager;
import hellfirepvp.astralsorcery.common.research.SyncPlayerProgress;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktSyncPlayerProgress
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktSyncPlayerProgress extends PlayPacketHandler.ToClient<PktSyncPlayerProgress.Request> {

    static final CustomPacketPayload.Type<Request> TYPE = makeType("sync_player_progress");
    static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.composite(
            SyncPlayerProgress.SYNC_CODEC,
            Request::progress,
            Request::new
    );

    public static final PktSyncPlayerProgress HANDLER = new PktSyncPlayerProgress();

    private PktSyncPlayerProgress() {
        super(TYPE);
    }

    public static Request newRequest(PlayerProgress progress) {
        return new Request(SyncPlayerProgress.sync(progress));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> ResearchManager.setClientProgress(payload.progress.load()));
    }

    public static record Request(SyncPlayerProgress progress) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
