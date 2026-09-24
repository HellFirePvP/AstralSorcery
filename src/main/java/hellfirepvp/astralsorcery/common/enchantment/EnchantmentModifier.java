/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.enchantment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.enchantment.Enchantment;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: EnchantmentModifier
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class EnchantmentModifier {

    public static final Codec<EnchantmentModifier> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            CodecUtil.enumCodec(Type.class).fieldOf("type").forGetter(EnchantmentModifier::getType),
            RegistryFixedCodec.create(Registries.ENCHANTMENT).optionalFieldOf("enchantment").forGetter(EnchantmentModifier::getEnchantment),
            Codec.INT.fieldOf("modifier").forGetter(EnchantmentModifier::getModifier)
    ).apply(inst, EnchantmentModifier::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentModifier> STREAM_CODEC = StreamCodec.composite(
            CodecUtil.enumStreamCodec(Type.class),
            EnchantmentModifier::getType,
            ByteBufCodecs.optional(ByteBufCodecs.holderRegistry(Registries.ENCHANTMENT)),
            EnchantmentModifier::getEnchantment,
            ByteBufCodecs.INT,
            EnchantmentModifier::getModifier,
            EnchantmentModifier::new);

    private final Type type;
    @Nullable
    private final Holder<Enchantment> enchantment;
    private final int modifier;

    private EnchantmentModifier(Type type, @Nullable Holder<Enchantment> enchantment, int modifier) {
        this(type, Optional.ofNullable(enchantment), modifier);
    }

    private EnchantmentModifier(Type type, Optional<Holder<Enchantment>> enchantment, int modifier) {
        this.type = type;
        this.enchantment = enchantment.orElse(null);
        this.modifier = modifier;
    }

    public static EnchantmentModifier addLevel(Holder<Enchantment> enchantment, int added) {
        return new EnchantmentModifier(Type.ADD_TO_SPECIFIC, enchantment, added);
    }

    public static EnchantmentModifier addToExistingLevel(Holder<Enchantment> enchantment, int added) {
        return new EnchantmentModifier(Type.ADD_TO_EXISTING_SPECIFIC, enchantment, added);
    }

    public static EnchantmentModifier addEnchantmentLevel(Type type, Holder<Enchantment> enchantment, int added) {
        return new EnchantmentModifier(type, enchantment, added);
    }

    public static EnchantmentModifier addToAll(int added) {
        return new EnchantmentModifier(Type.ADD_TO_EXISTING_ALL, Optional.empty(), added);
    }

    public Type getType() {
        return this.type;
    }

    public Optional<Holder<Enchantment>> getEnchantment() {
        return Optional.ofNullable(this.enchantment);
    }

    public int getModifier() {
        return this.modifier;
    }

    public EnchantmentModifier copyWithModifier(int newModifier) {
        return new EnchantmentModifier(this.getType(), this.getEnchantment(), newModifier);
    }

    public MutableComponent getDisplay() {
        String typeStr = this.getType().getDisplayFormat();
        MutableComponent levelCmp = Component.translatable(this.getModifierChangeDisplay());
        MutableComponent enchName = this.getEnchantment().map(Holder::value).map(ench -> {
            return Component.empty().append(ench.description());
        }).orElse(Component.empty());

        return Component.translatable(typeStr, String.valueOf(this.getModifier()), levelCmp, enchName)
                .withStyle(ChatFormatting.BLUE);
    }

    public String getModifierChangeDisplay() {
        String levelStr = this.modifier > 1 ? "more" : "one";
        return String.format("astralsorcery.enchantment_modifier.level.%s", levelStr);
    }

    public boolean isEmpty() {
        return this.getModifier() == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        EnchantmentModifier that = (EnchantmentModifier) o;
        return modifier == that.modifier && type == that.type && Objects.equals(enchantment, that.enchantment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, enchantment, modifier);
    }

    public enum Type {

        ADD_TO_SPECIFIC(true),
        ADD_TO_EXISTING_SPECIFIC(true),
        ADD_TO_EXISTING_ALL;

        private final boolean hasEnchantment;

        Type() {
            this(false);
        }

        Type(boolean hasEnchantment) {
            this.hasEnchantment = hasEnchantment;
        }

        public boolean hasEnchantment() {
            return this.hasEnchantment;
        }

        public String getDisplayFormat() {
            return String.format("astralsorcery.enchantment_modifier.%s.name", this.name().toLowerCase(Locale.ROOT));
        }
    }
}
