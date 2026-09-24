/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.codec;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Function7;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CodecUtil
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CodecUtil {

    private static final Gson GSON = new GsonBuilder().create();

    private CodecUtil() {}

    public static <E extends Enum<E>> Codec<E> enumCodec(Class<E> enumClass) {
        return Codec.INT.xmap(ordinal -> MiscUtil.getEnumEntry(enumClass, ordinal), Enum::ordinal);
    }

    public static <E extends Enum<E>> StreamCodec<ByteBuf, E> enumStreamCodec(Class<E> enumClass) {
        return StreamCodec.of((buf, e) -> buf.writeInt(e.ordinal()), buf -> MiscUtil.getEnumEntry(enumClass, buf.readInt()));
    }

    @Deprecated
    public static Codec<UUID> uuidCodec() {
        return UUIDUtil.CODEC;
    }

    public static Codec<String> stringSized(int maxLength) {
        return stringSized(0, maxLength);
    }

    public static Codec<String> stringSized(int minLength, int maxLength) {
        return Codec.STRING.comapFlatMap(str -> {
            if (str.length() < minLength || str.length() > maxLength) {
                return DataResult.error(() -> "String length out of bounds: " + str.length() + ", expected range [" + minLength + "-" + maxLength + "]");
            }
            return DataResult.success(str);
        }, Function.identity());
    }

    public static Codec<BlockPos> stringBlockPos() {
        return stringifyCodec(str -> {
            String[] parts = str.split(";");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid BlockPos string format: " + str);
            }
            return new BlockPos(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
        }, pos -> String.format("%s;%s;%s", pos.getX(), pos.getY(), pos.getZ()));
    }

    public static Codec<Integer> stringInteger() {
        return Codec.STRING.comapFlatMap(str -> {
            try {
                return DataResult.success(Integer.parseInt(str));
            } catch (NumberFormatException e) {
                return DataResult.error(() -> "Invalid integer string: " + str);
            }
        }, String::valueOf);
    }

    public static Codec<GameProfile> gameProfileCodec() {
        return RecordCodecBuilder.create(inst -> inst.group(
                UUIDUtil.CODEC.fieldOf("id").forGetter(GameProfile::getId),
                Codec.STRING.fieldOf("name").forGetter(GameProfile::getName)
        ).apply(inst, GameProfile::new));
    }

    public static <T> Codec<T> stringifyCodec(Function<String, T> fromString, Function<T, String> toString) {
        return Codec.STRING.comapFlatMap(str -> {
            try {
                return DataResult.success(fromString.apply(str));
            } catch (Exception e) {
                return DataResult.error(() -> "Failed to parse string: " + str + " - " + e.getMessage());
            }
        }, toString);
    }

    public static <T> StreamCodec<ByteBuf, TagKey<T>> tagKeyStreamCodec(ResourceKey<? extends Registry<T>> registry) {
        return ResourceLocation.STREAM_CODEC.map(key -> TagKey.create(registry, key), TagKey::location);
    }

    public static StreamCodec<ByteBuf, UUID> uuidStreamCodec() {
        return StreamCodec.of((buf, id) -> {
            buf.writeLong(id.getMostSignificantBits());
            buf.writeLong(id.getLeastSignificantBits());
        }, buf -> new UUID(buf.readLong(), buf.readLong()));
    }

    public static StreamCodec<FriendlyByteBuf, JsonObject> rawJsonObjectStreamCodec() {
        return StreamCodec.of((buf, obj) -> {
            buf.writeUtf(GSON.toJson(obj), Integer.MAX_VALUE);
        }, buf -> {
            String json = buf.readUtf(Integer.MAX_VALUE);
            return GSON.fromJson(json, JsonObject.class);
        });
    }

    public static <B extends ByteBuf, K> StreamCodec<B, Pair<K, K>> pairStreamCodec(StreamCodec<B, K> elementCodec) {
        return pairStreamCodec(elementCodec, elementCodec);
    }

    public static <B extends ByteBuf, K, V> StreamCodec<B, Pair<K, V>> pairStreamCodec(StreamCodec<B, K> leftCodec, StreamCodec<B, V> rightCodec) {
        return StreamCodec.of(
                (buf, pair) -> {
                    leftCodec.encode(buf, pair.getFirst());
                    rightCodec.encode(buf, pair.getSecond());
                },
                buf -> Pair.of(
                        leftCodec.decode(buf),
                        rightCodec.decode(buf)
                )
        );
    }

    public static <D, P> RecordCodecBuilder<D, P> defaulted(Codec<P> codec, String field, Supplier<P> _default, Function<D, P> getter) {
        return defaulted(codec, field, _default, getter, Objects::isNull);
    }

    public static <D, P> RecordCodecBuilder<D, P> defaulted(Codec<P> codec, String field, Supplier<P> _default, Function<D, P> getter, Predicate<P> emptyValidator) {
        return codec.optionalFieldOf(field)
                .xmap(obj -> obj.orElse(_default.get()), value -> emptyValidator.test(value) ? Optional.empty() : Optional.of(value))
                .forGetter(getter);
    }

    public static <D, P> RecordCodecBuilder<D, P> lenientDefaulted(Codec<P> codec, String field, Supplier<P> _default, Function<D, P> getter) {
        return lenientDefaulted(codec, field, _default, getter, Objects::isNull);
    }

    public static <D, P> RecordCodecBuilder<D, P> lenientDefaulted(Codec<P> codec, String field, Supplier<P> _default, Function<D, P> getter, Predicate<P> emptyValidator) {
        return codec.lenientOptionalFieldOf(field)
                .xmap(obj -> obj.orElse(_default.get()), value -> emptyValidator.test(value) ? Optional.empty() : Optional.of(value))
                .forGetter(getter);
    }

    public static <D, P> RecordCodecBuilder<D, Optional<P>> optional(Codec<P> codec, String field, Function<D, P> getter) {
        return codec.optionalFieldOf(field).forGetter(dataObj -> Optional.ofNullable(getter.apply(dataObj)));
    }

    public static <T> Codec<T> registryOr(Registry<T> registry, Codec<T> nonRegistryCodec) {
        return Codec.either(registry.byNameCodec(), nonRegistryCodec)
                .xmap(either -> either.left().orElse(either.right().orElseThrow()),
                      value -> {
                          if (registry.containsValue(value)) {
                              return Either.left(value);
                          }
                          return Either.right(value);
                      });
    }

    public

    static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> streamComposite(
            final StreamCodec<? super B, T1> codec1,
            final Function<C, T1> getter1,
            final StreamCodec<? super B, T2> codec2,
            final Function<C, T2> getter2,
            final StreamCodec<? super B, T3> codec3,
            final Function<C, T3> getter3,
            final StreamCodec<? super B, T4> codec4,
            final Function<C, T4> getter4,
            final StreamCodec<? super B, T5> codec5,
            final Function<C, T5> getter5,
            final StreamCodec<? super B, T6> codec6,
            final Function<C, T6> getter6,
            final StreamCodec<? super B, T7> codec7,
            final Function<C, T7> getter7,
            final Function7<T1, T2, T3, T4, T5, T6, T7, C> factory
    ) {
        return new StreamCodec<B, C>() {
            @Override
            public C decode(B p_330310_) {
                T1 t1 = codec1.decode(p_330310_);
                T2 t2 = codec2.decode(p_330310_);
                T3 t3 = codec3.decode(p_330310_);
                T4 t4 = codec4.decode(p_330310_);
                T5 t5 = codec5.decode(p_330310_);
                T6 t6 = codec6.decode(p_330310_);
                T7 t7 = codec7.decode(p_330310_);
                return factory.apply(t1, t2, t3, t4, t5, t6, t7);
            }

            @Override
            public void encode(B p_332052_, C p_331912_) {
                codec1.encode(p_332052_, getter1.apply(p_331912_));
                codec2.encode(p_332052_, getter2.apply(p_331912_));
                codec3.encode(p_332052_, getter3.apply(p_331912_));
                codec4.encode(p_332052_, getter4.apply(p_331912_));
                codec5.encode(p_332052_, getter5.apply(p_331912_));
                codec6.encode(p_332052_, getter6.apply(p_331912_));
                codec7.encode(p_332052_, getter7.apply(p_331912_));
            }
        };
    }
}
