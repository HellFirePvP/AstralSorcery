/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.world.AuxiliaryLightManager;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktSyncAuxiliaryLightManager
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktSyncAuxiliaryLightManager extends PlayPacketHandler.ToClient<PktSyncAuxiliaryLightManager.Request> {

    static final CustomPacketPayload.Type<Request> TYPE = makeType("sync_aux_light_manager");
    static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            Request::pos,
            ByteBufCodecs.INT,
            Request::lightLevel,
            Request::new
    );

    public static final PktSyncAuxiliaryLightManager HANDLER = new PktSyncAuxiliaryLightManager();

    private PktSyncAuxiliaryLightManager() {
        super(TYPE);
    }

    public static Request updateLight(BlockPos pos, int light) {
        return new Request(pos, light);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = context.player().level();
            AuxiliaryLightManager lightMgr = level.getAuxLightManager(payload.pos());
            if (lightMgr == null) return;
            lightMgr.setLightAt(payload.pos(), payload.lightLevel());
        });
    }

    public record Request(BlockPos pos, int lightLevel) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
