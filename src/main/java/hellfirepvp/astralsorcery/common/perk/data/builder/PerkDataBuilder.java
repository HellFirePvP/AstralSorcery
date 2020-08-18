package hellfirepvp.astralsorcery.common.perk.data.builder;

import com.google.common.collect.ImmutableList;
import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.perk.PerkConverter;
import hellfirepvp.astralsorcery.common.perk.data.PerkTypeHandler;
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeConverterPerk;
import hellfirepvp.astralsorcery.common.perk.modifier.AttributeModifierPerk;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkDataBuilder
 * Created by HellFirePvP
 * Date: 14.08.2020 / 18:34
 */
public class PerkDataBuilder<T extends AbstractPerk> {

    private final T perk;
    private final List<ResourceLocation> connections = new ArrayList<>();

    public PerkDataBuilder(T perk) {
        this.perk = perk;
    }

    public static PerkBuilder<AbstractPerk> builder() {
        return new PerkBuilder<>(PerkTypeHandler.DEFAULT);
    }

    public static <T extends AbstractPerk> PerkBuilder<T> ofType(PerkTypeHandler.Type<T> perkType) {
        return new PerkBuilder<>(perkType);
    }

    public PerkDataBuilder<T> setName(String perkDisplayName) {
        this.perk.setName(perkDisplayName);
        return this;
    }

    public PerkDataBuilder<T> addModifier(float modifier, ModifierType mode, PerkAttributeType type) {
        if (!(this.perk instanceof AttributeModifierPerk)) {
            throw new IllegalArgumentException("Cannot add modifiers to non-modifier perks!");
        }
        ((AttributeModifierPerk) this.perk).addModifier(modifier, mode, type);
        return this;
    }

    public PerkDataBuilder<T> addModifier(PerkAttributeModifier modifier) {
        if (!(this.perk instanceof AttributeModifierPerk)) {
            throw new IllegalArgumentException("Cannot add modifiers to non-modifier perks!");
        }
        ((AttributeModifierPerk) this.perk).addModifier(modifier);
        return this;
    }

    public PerkDataBuilder<T> addConverter(PerkConverter converter) {
        if (!(this.perk instanceof AttributeConverterPerk)) {
            throw new IllegalArgumentException("Cannot add converter to non-converter perks!");
        }
        ((AttributeConverterPerk) this.perk).addConverter(converter);
        return this;
    }

    public PerkDataBuilder<T> modify(Consumer<T> recipeFn) {
        recipeFn.accept(this.perk);
        return this;
    }

    public PerkDataBuilder<?> chain(PerkDataBuilder<?> other) {
        this.connect(other.perk.getRegistryName());
        return other;
    }

    public PerkDataBuilder<T> connect(PerkDataBuilder<?> other) {
        return this.connect(other.perk.getRegistryName());
    }

    public PerkDataBuilder<T> connect(ResourceLocation key) {
        this.connections.add(key);
        return this;
    }

    public PerkDataBuilder<T> build(Consumer<PerkDataProvider.FinishedPerk> consumerIn) {
        consumerIn.accept(new PerkDataProvider.FinishedPerk(this.perk, ImmutableList.copyOf(this.connections)));
        return this;
    }

    public static class PerkBuilder<T extends AbstractPerk> {

        private final PerkTypeHandler.Type<T> perkType;

        private PerkBuilder(PerkTypeHandler.Type<T> perkType) {
            this.perkType = perkType;
        }

        public PerkDataBuilder<T> create(ResourceLocation perkKey, float x, float y) {
            T perk = this.perkType.convert(perkKey, x, y);
            if (!perkType.getKey().equals(PerkTypeHandler.DEFAULT.getKey())) {
                perk.setCustomPerkType(perkType.getKey());
            }
            return new PerkDataBuilder<>(perk);
        }
    }
}
