/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CharacterCodec
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class CharacterCodec {

    public static final Codec<Character> CODEC = Codec.STRING.comapFlatMap(str -> {
        if (str.length() != 1) {
            return DataResult.error(() -> "Expected a single character, got: " + str);
        }
        return DataResult.success(str.charAt(0));
    }, String::valueOf);

    public static final Codec<Character> CODEC_NON_BLANK = CODEC.comapFlatMap(str -> {
        if (String.valueOf(str).isBlank()) {
            return DataResult.error(() -> "Expected a non-blank character, got: " + str);
        }
        return DataResult.success(str);
    }, Function.identity());

    public static final StreamCodec<ByteBuf, Character> STREAM_CODEC = StreamCodec.of(
            (buf, character) -> buf.writeChar(character),
            ByteBuf::readChar
    );

    private CharacterCodec() {}

}
