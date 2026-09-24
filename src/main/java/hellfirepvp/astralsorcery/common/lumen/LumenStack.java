/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.lib.LumenAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenStack
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class LumenStack {

    public static final Codec<LumenStack> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            RegistriesAS.REGISTRY_LUMEN.byNameCodec().optionalFieldOf("lumen").forGetter(LumenStack::getLumenOpt),
            Codec.INT.fieldOf("amount").forGetter(LumenStack::getAmount)
    ).apply(inst, LumenStack::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, LumenStack> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.optional(ByteBufCodecs.registry(RegistriesAS.KEY_LUMEN)),
            LumenStack::getLumenOpt,
            ByteBufCodecs.INT,
            LumenStack::getAmount,
            LumenStack::new
    );

    public static final LumenStack EMPTY = new LumenStack(Optional.empty(), 0);
    public static final int FLASK_VALUE = 500;

    @Nullable
    private Lumen lumen;
    private int amount;

    private LumenStack(Optional<Lumen> lumen, int amount) {
        this(lumen.orElse(null), amount);
    }

    private LumenStack(@Nullable Lumen lumen, int amount) {
        this.lumen = lumen;
        this.amount = amount;
        this.updateEmpty();
    }

    public static LumenStack of(@Nonnull Lumen lumen, int amount) {
        return new LumenStack(lumen, amount);
    }

    private Optional<Lumen> getLumenOpt() {
        return Optional.ofNullable(this.lumen);
    }

    public LumenStack copy() {
        return new LumenStack(this.lumen, this.amount);
    }

    public LumenStack copyWithAmount(int amount) {
        return new LumenStack(this.lumen, amount);
    }

    @Nonnull
    public Lumen getLumen() {
        if (this.isEmpty()) return LumenAS.NONE.get();
        return this.lumen;
    }

    public boolean isSameLumen(LumenStack otherStack) {
        return this.is(otherStack.getLumen());
    }

    public boolean is(Supplier<Lumen> lumen) {
        return this.is(lumen.get());
    }

    public boolean is(Lumen lumen) {
        if (this.isEmpty()) return lumen == LumenAS.NONE.get();
        return this.lumen == lumen;
    }

    public int getAmount() {
        if (this.isEmpty()) return 0;
        return this.amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
        this.updateEmpty();
    }

    public void grow(int amount) {
        this.amount += amount;
        this.updateEmpty();
    }

    public void shrink(int amount) {
        this.amount -= amount;
        this.updateEmpty();
    }

    public boolean isEmpty() {
        return this.lumen == null || this.amount <= 0;
    }

    private void updateEmpty() {
        if (this.isEmpty() || this.lumen == LumenAS.NONE.get()) {
            this.lumen = null;
            this.amount = 0;
        }
    }
}
