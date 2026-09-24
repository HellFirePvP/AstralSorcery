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
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CountIngredient
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record CountIngredient(Ingredient ingredient, int count) {

    public static final Codec<CountIngredient> CODEC_NONEMPTY = RecordCodecBuilder.create(inst -> inst.group(
            Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(CountIngredient::ingredient),
            ExtraCodecs.intRange(1, 64).fieldOf("count").forGetter(CountIngredient::count)
    ).apply(inst, CountIngredient::new));

    public static final Codec<CountIngredient> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            Ingredient.CODEC.fieldOf("ingredient").forGetter(CountIngredient::ingredient),
            ExtraCodecs.intRange(1, 64).fieldOf("count").forGetter(CountIngredient::count)
    ).apply(inst, CountIngredient::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CountIngredient> CONTENTS_STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            CountIngredient::ingredient,
            ByteBufCodecs.INT,
            CountIngredient::count,
            CountIngredient::new);

    public static final StreamCodec<RegistryFriendlyByteBuf, CountIngredient> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC,
            CountIngredient::ingredient,
            ByteBufCodecs.INT,
            CountIngredient::count,
            CountIngredient::new);

    public boolean isEmpty() {
        return this.ingredient.isEmpty() || this.count <= 0;
    }
}
