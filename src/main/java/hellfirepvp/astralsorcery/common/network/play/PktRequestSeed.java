/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import hellfirepvp.astralsorcery.common.util.level.LevelEffectSeedCache;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktRequestSeed
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktRequestSeed extends PlayPacketHandler.BiDirectional<PktRequestSeed.Request> {

    static final CustomPacketPayload.Type<Request> TYPE = makeType("request_seed");
    static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.composite(
            ResourceKey.streamCodec(Registries.DIMENSION),
            Request::dimKey,
            ByteBufCodecs.INT,
            Request::session,
            ByteBufCodecs.VAR_LONG,
            Request::seed,
            Request::new
    );

    public static final PktRequestSeed HANDLER = new PktRequestSeed();

    private PktRequestSeed() {
        super(TYPE);
    }

    public static Request newRequest(ResourceKey<Level> dimKey, int session) {
        return new Request(dimKey, session, -1);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handleClient(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> LevelEffectSeedCache.updateClientSeedCache(payload.dimKey(), payload.session(), payload.seed()));
    }

    @Override
    public void handleServer(Request payload, IPayloadContext context) {
        context.enqueueWork(() -> context.reply(payload.response(LevelEffectSeedCache.getServerWorldSeed(payload.dimKey()))));
    }

    public static record Request(ResourceKey<Level> dimKey, int session, long seed) implements CustomPacketPayload {

        public Request response(long seed) {
            return new Request(this.dimKey(), this.session(), seed);
        }

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

}
