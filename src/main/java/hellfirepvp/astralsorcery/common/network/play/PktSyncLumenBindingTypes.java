/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.network.play;

import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.lumen.binding.LumenBindingType;
import hellfirepvp.astralsorcery.common.lumen.binding.data.LumenBindingTypeLoader;
import hellfirepvp.astralsorcery.common.network.PlayPacketHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PktSyncLumenBindingTypes
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PktSyncLumenBindingTypes extends PlayPacketHandler.ToClient<PktSyncLumenBindingTypes.Request> {

    static final CustomPacketPayload.Type<Request> TYPE = makeType("sync_lumen_binding_types");
    static final StreamCodec<RegistryFriendlyByteBuf, Request> CODEC = StreamCodec.composite(
            ByteBufCodecs.map(size -> new HashMap<>(),
                    ResourceLocation.STREAM_CODEC,
                    ByteBufCodecs.fromCodecWithRegistries(LumenBindingType.CODEC)),
            Request::bindings,
            ByteBufCodecs.map(size -> new HashMap<>(),
                    ResourceKey.streamCodec(RegistriesAS.KEY_LUMEN),
                    ResourceLocation.STREAM_CODEC),
            Request::lumenApplicationMapping,
            Request::new
    );

    public static final PktSyncLumenBindingTypes HANDLER = new PktSyncLumenBindingTypes();

    private PktSyncLumenBindingTypes() {
        super(TYPE);
    }

    public static Request newRequest() {
        return new Request(LumenBindingTypeLoader.getInstance().getBindings(LogicalSide.SERVER),
                LumenBindingTypeLoader.getInstance().getLumenApplicationMapping(LogicalSide.SERVER));
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Request> codec() {
        return CODEC;
    }

    @Override
    public void handle(Request payload, IPayloadContext context) {
        context.enqueueWork(() ->
                LumenBindingTypeLoader.getInstance().updateClientBindings(payload.bindings(), payload.lumenApplicationMapping()));
    }

    public static record Request(Map<ResourceLocation, LumenBindingType> bindings,
                                 Map<ResourceKey<Lumen>, ResourceLocation> lumenApplicationMapping) implements CustomPacketPayload {

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

}
