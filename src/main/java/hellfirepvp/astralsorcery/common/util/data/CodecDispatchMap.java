/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CodecDispatchMap
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CodecDispatchMap<T extends CodecDispatchMap.DataEntry<? extends E>, E> {

    private final Map<ResourceLocation, T> dataMap = new HashMap<>();
    private final Function<E, ResourceLocation> nameResolver;

    public CodecDispatchMap(Function<E, ResourceLocation> nameResolver) {
        this.nameResolver = nameResolver;
    }

    public Codec<E> byNameCodec() {
        return ResourceLocation.CODEC
                .dispatch(this.nameResolver, key -> this.get(key).map(DataEntry::getCodec).orElseThrow());
    }

    public StreamCodec<RegistryFriendlyByteBuf, E> streamCodec() {
        StreamCodec<RegistryFriendlyByteBuf, ResourceLocation> keyCodec = MiscUtil.cast(ResourceLocation.STREAM_CODEC);
        return keyCodec.dispatch(this.nameResolver, key -> this.get(key).map(DataEntry::getStreamCodec).orElseThrow());
    }

    public void register(T entry) {
        this.dataMap.put(entry.getKey(), entry);
    }

    public Optional<T> get(ResourceLocation key) {
        return Optional.ofNullable(this.dataMap.get(key));
    }

    public abstract static class DataEntry<E> {

        public abstract ResourceLocation getKey();

        public abstract MapCodec<E> getCodec();

        public abstract StreamCodec<RegistryFriendlyByteBuf, E> getStreamCodec();

    }

}
