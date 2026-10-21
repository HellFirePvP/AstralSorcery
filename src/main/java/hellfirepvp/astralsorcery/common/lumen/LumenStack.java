/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenStack
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class LumenStack {

    @SuppressWarnings("deprecation")
    public static final Codec<Holder<Lumen>> LUMEN_NON_EMPTY_CODEC = RegistriesAS.REGISTRY_LUMEN
            .holderByNameCodec()
            .validate((holder) -> holder.is(LumenAS.NONE)
                    ? DataResult.error(() -> "Lumen must not be astralsorcery:none")
                    : DataResult.success(holder)
            );
    /**
     * @deprecated Use {@link NON_EMPTY_CODEC} or {@link OPTIONAL_CODEC} instead of this
     */
    @Deprecated
    public static final Codec<LumenStack> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistriesAS.REGISTRY_LUMEN.byNameCodec().optionalFieldOf("lumen").forGetter((stack) -> Optional.of(stack.getLumen())),
            Codec.INT.fieldOf("amount").forGetter(LumenStack::getAmount)
    ).apply(inst, (lumen, amount) -> lumen.map(lumen1 -> new LumenStack(lumen1, amount)).orElse(LumenStack.EMPTY)));
    public static final Codec<LumenStack> NON_EMPTY_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LUMEN_NON_EMPTY_CODEC.fieldOf("lumen").forGetter(LumenStack::getLumenHolder),
            ExtraCodecs.POSITIVE_INT.fieldOf("amount").forGetter(LumenStack::getAmount)
    ).apply(instance, LumenStack::new));
    public static final Codec<LumenStack> OPTIONAL_CODEC = ExtraCodecs.optionalEmptyMap(NON_EMPTY_CODEC).xmap(
            (optional) -> optional.orElse(LumenStack.EMPTY),
            (lumenStack) -> Optional.ofNullable(lumenStack).filter(LumenStack::isNotEmpty)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<Lumen>> LUMEN_STREAM_CODEC = ByteBufCodecs.holderRegistry(RegistriesAS.KEY_LUMEN);
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenStack> STREAM_CODEC = StreamCodec.composite(
            LUMEN_STREAM_CODEC,
            LumenStack::getLumenHolder,
            ByteBufCodecs.INT,
            LumenStack::getAmount,
            LumenStack::new
    );

    public static final LumenStack EMPTY = new LumenStack(null);
    public static final int FLASK_VALUE = 500;

    @Nullable
    private final Holder<Lumen> lumen;
    private int amount;

    private LumenStack(Lumen lumen, int amount) {
        this(RegistriesAS.REGISTRY_LUMEN.wrapAsHolder(lumen), amount);
    }

    private LumenStack(@Nonnull Holder<Lumen> lumen, int amount) {
        this.lumen = lumen;
        this.amount = amount;
    }

    private LumenStack(@Nullable Void unused) {
        this.lumen = null;
        this.amount = 0;
    }

    public static LumenStack of(@Nonnull Lumen lumen, int amount) {
        return new LumenStack(lumen, amount);
    }

    public static LumenStack of(@Nonnull Holder<Lumen> lumen, int amount) {
        return new LumenStack(lumen, amount);
    }

    public LumenStack copy() {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            return new LumenStack(Objects.requireNonNull(this.lumen), this.amount);
        }
    }

    @Nonnull
    public LumenStack copyWithAmount(int amount) {
        if (this.isEmpty()) {
            return EMPTY;
        } else {
            var stack = this.copy();
            stack.setAmount(amount);
            return stack;
        }
    }

    @Nonnull
    public Holder<Lumen> getLumenHolder() {
        return this.isEmpty() ? LumenAS.NONE : Objects.requireNonNull(this.lumen);
    }

    @Nonnull
    public Lumen getLumen() {
        return getLumenHolder().value();
    }

    public boolean isSameLumen(LumenStack otherStack) {
        return this.is(otherStack.getLumen());
    }

    public boolean is(Lumen lumen) {
        return this.getLumen() == lumen;
    }

    public boolean is(ResourceKey<Lumen> key) {
        return this.getLumenHolder().is(key);
    }

    public boolean is(TagKey<Lumen> tagKey) {
        return this.getLumenHolder().is(tagKey);
    }

    @SuppressWarnings("deprecation")
    public boolean is(Holder<Lumen> lumen) {
        return this.getLumenHolder().is(lumen);
    }

    public int getAmount() {
        return this.isEmpty() ? 0 : this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void grow(int amount) {
        this.amount += amount;
    }

    public void shrink(int amount) {
        this.amount -= amount;
    }

    @SuppressWarnings("deprecation")
    public boolean isEmpty() {
        return this == EMPTY || this.lumen == null || this.lumen.is(LumenAS.NONE) || this.amount <= 0;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }
}
