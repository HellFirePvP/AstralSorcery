/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.codec;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: HashComparedCodec
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class HashComparedCodec {

    public static <T> Codec<T> wrap(Codec<T> codec, LongSupplier globalHashSupplier, Supplier<T> blankSupplier) {
        return hashVersionCodec(codec).xmap(hashVersion -> {
            if (hashVersion.hash() != globalHashSupplier.getAsLong()) {
                return blankSupplier.get();
            }
            return hashVersion.value();
        }, value -> new HashVersionValue<>(globalHashSupplier.getAsLong(), value));
    }

    private static <T> Codec<HashVersionValue<T>> hashVersionCodec(Codec<T> valueCodec) {
        return RecordCodecBuilder.create(inst -> inst.group(
                Codec.LONG.fieldOf("hash").forGetter(HashVersionValue::hash),
                valueCodec.fieldOf("value").forGetter(HashVersionValue::value)
        ).apply(inst, HashVersionValue::new));
    }

    private record HashVersionValue<T>(long hash, T value) {
    }
}
