/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;

import java.util.*;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: RandomWeightedList
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class RandomWeightedList<T> {

    private final List<Entry<T>> entries;

    private RandomWeightedList(List<Entry<T>> entries) {
        this.entries = entries;
    }

    public static <T> Codec<RandomWeightedList<T>> codec(Codec<T> elementCodec) {
        return RecordCodecBuilder.create(inst -> inst.group(
                entryCodec(elementCodec).listOf().fieldOf("entries").forGetter(palette -> palette.entries)
        ).apply(inst, RandomWeightedList::new));
    }

    private static <T> Codec<Entry<T>> entryCodec(Codec<T> elementCodec) {
        return RecordCodecBuilder.create(inst -> inst.group(
                elementCodec.fieldOf("state").forGetter(Entry::value),
                Codec.INT.fieldOf("weight").forGetter(Entry::weight)
        ).apply(inst, Entry::new));
    }

    public static <T> StreamCodec<RegistryFriendlyByteBuf, RandomWeightedList<T>> streamCodec(StreamCodec<RegistryFriendlyByteBuf, T> elementStreamCodec) {
        return entryStreamCodec(elementStreamCodec).apply(ByteBufCodecs.list())
                .map(RandomWeightedList::new, palette -> palette.entries);
    }

    private static <T> StreamCodec<RegistryFriendlyByteBuf, Entry<T>> entryStreamCodec(StreamCodec<RegistryFriendlyByteBuf, T> elementStreamCodec) {
        return StreamCodec.composite(
                elementStreamCodec,
                Entry::value,
                ByteBufCodecs.INT,
                Entry::weight,
                Entry::new);
    }

    public static <T> Builder<T> of() {
        return new Builder<>();
    }

    public Optional<T> getRandomEntry(RandomSource rand) {
        return MiscUtil.getWeightedRandomEntry(this.entries, rand, Entry::weight).map(Entry::value);
    }

    public int getSize() {
        return this.entries.size();
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public static class Builder<T> {

        private final Map<T, Integer> entries = new HashMap<>();

        private Builder() {}

        public Builder<T> add(T value, int weight) {
            this.entries.put(value, weight);
            return this;
        }

        public RandomWeightedList<T> build() {
            return new RandomWeightedList<>(MapStream.of(this.entries).toList(Entry::new));
        }
    }

    private record Entry<T>(T value, int weight) {}
}
