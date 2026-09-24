/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.data.builder;

import hellfirepvp.astralsorcery.common.perk.convert.PerkAttributeConverter;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.tree.*;
import hellfirepvp.astralsorcery.common.perk.tree.requirement.PerkRequirement;
import hellfirepvp.astralsorcery.common.perk.type.base.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.util.data.PerkTypeRegistryObject;
import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: PerkDataBuilder
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class PerkDataBuilder<T extends AbstractPerk<?>> {

    final T perk;
    final Set<ResourceLocation> connections = new HashSet<>();

    private PerkDataBuilder(T perk) {
        this.perk = perk;
    }

    public static <T extends AbstractPerk<?>> PerkBuilder<T> builder(PerkType<T> type) {
        return builder(() -> type);
    }

    public static <T extends AbstractPerk<?>> PerkBuilder<T> builder(PerkTypeRegistryObject<T> type) {
        return builder(() -> type.type().value());
    }

    public static <T extends AbstractPerk<?>> PerkBuilder<T> builder(Supplier<PerkType<T>> type) {
        return new PerkBuilder<>(type);
    }

    public PerkDataBuilder<T> setNameKey(String nameKey) {
        this.perk.setNameKey(nameKey);
        return this;
    }

    public PerkDataBuilder<T> setCategory(PerkCategory category) {
        this.perk.setCategory(category);
        return this;
    }

    public PerkDataBuilder<T> addRequirement(PerkRequirement requirement) {
        ProgressPerk<?> pPerk = this.assertPerkType(ProgressPerk.class);
        pPerk.addRequirement(requirement);
        return this;
    }

    public PerkDataBuilder<T> addConverter(Supplier<PerkAttributeConverter> converter) {
        AttributeConverterPerk<?> cPerk = this.assertPerkType(AttributeConverterPerk.class);
        cPerk.addConverter(converter.get());
        return this;
    }

    public PerkDataBuilder<T> addModifier(float modifier, ModifierType mode, PerkAttributeType type) {
        return this.addModifier(modifier, mode, () -> type);
    }

    public PerkDataBuilder<T> addModifier(float modifier, ModifierType mode, Supplier<? extends PerkAttributeType> type) {
        AttributeModifierPerk<?> mPerk = this.assertPerkType(AttributeModifierPerk.class);
        mPerk.addModifier(new PerkAttributeModifier(type.get(), mode, modifier));
        return this;
    }

    public PerkDataBuilder<T> addCustomModifier(Supplier<PerkAttributeModifier> modifier) {
        AttributeModifierPerk<?> mPerk = this.assertPerkType(AttributeModifierPerk.class);
        mPerk.addModifier(modifier.get());
        return this;
    }

    public PerkDataBuilder<T> modify(Consumer<T> perkFn) {
        perkFn.accept(this.perk);
        return this;
    }

    public PerkDataBuilder<T> connect(PerkDataBuilder<?>... others) {
        for (PerkDataBuilder<?> other : others) {
            this.connect(other);
        }
        return this;
    }

    public PerkDataBuilder<T> connect(ResourceLocation... otherKeys) {
        for (ResourceLocation other : otherKeys) {
            this.connect(other);
        }
        return this;
    }

    public PerkDataBuilder<T> connect(PerkDataBuilder<?> other) {
        return this.connect(other.perk.getKey());
    }

    public PerkDataBuilder<T> connect(ResourceLocation key) {
        this.connections.add(key);
        return this;
    }

    public PerkDataBuilder<T> build(Consumer<PerkDataProvider.BuiltPerk> registrar) {
        registrar.accept(PerkDataProvider.BuiltPerk.of(this));
        return this;
    }

    private <P extends AbstractPerk<?>> P assertPerkType(Class<P> perkClass) {
        if (!perkClass.isInstance(this.perk)) {
            throw new IllegalArgumentException(String.format("Perk is not of type %s (is %s)", perkClass.getSimpleName(), this.perk.getClass().getSimpleName()));
        }
        return MiscUtil.cast(this.perk);
    }

    public static class PerkBuilder<T extends AbstractPerk<?>> {

        private final Supplier<PerkType<T>> perkType;

        private PerkBuilder(Supplier<PerkType<T>> perkType) {
            this.perkType = perkType;
        }

        public PerkDataBuilder<T> create(ResourceLocation perkKey, float x, float y) {
            return new PerkDataBuilder<>(this.perkType.get().newBlankPerk(perkKey, x, y));
        }
    }
}
