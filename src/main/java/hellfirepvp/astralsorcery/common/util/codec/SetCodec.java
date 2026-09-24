/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.codec;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: SetCodec
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record SetCodec<E>(Codec<E> elementCodec) implements Codec<Set<E>> {

    public static <T> SetCodec<T> of(Codec<T> codec) {
        return new SetCodec<>(codec);
    }

    public static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, Set<V>> streamOp() {
        return codec -> ByteBufCodecs.collection(HashSet::new, codec);
    }

    @Override
    public <T> DataResult<T> encode(Set<E> input, DynamicOps<T> ops, T prefix) {
        final ListBuilder<T> builder = ops.listBuilder();
        for (final E element : input) {
            builder.add(elementCodec.encodeStart(ops, element));
        }
        return builder.build(prefix);
    }

    @Override
    public <T> DataResult<Pair<Set<E>, T>> decode(DynamicOps<T> ops, T input) {
        return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(stream -> {
            final DecoderState<T> decoder = new DecoderState<>(ops);
            stream.accept(decoder::accept);
            return decoder.build();
        });
    }

    @Override
    public String toString() {
        return "SetCodec[" + elementCodec + ']';
    }

    private class DecoderState<T> {

        private static final DataResult<Unit> INITIAL_RESULT = DataResult.success(Unit.INSTANCE, Lifecycle.stable());

        private final DynamicOps<T> ops;
        private final Set<E> elements = new HashSet<>();
        private DataResult<Unit> result = INITIAL_RESULT;

        private DecoderState(final DynamicOps<T> ops) {
            this.ops = ops;
        }

        public void accept(final T value) {
            final DataResult<Pair<E, T>> elementResult = elementCodec.decode(ops, value);
            elementResult.resultOrPartial().ifPresent(pair -> elements.add(pair.getFirst()));
            result = result.apply2stable((result, element) -> result, elementResult);
        }

        public DataResult<Pair<Set<E>, T>> build() {
            final T errors = ops.createList(Stream.empty());
            final Pair<Set<E>, T> pair = Pair.of(new HashSet<>(elements), errors);
            return result.map(ignored -> pair).setPartial(pair);
        }
    }
}
