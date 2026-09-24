/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CrystalAttributesComponent
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class CrystalAttributesComponent implements DynamicTooltipComponent {

    public static final Codec<CrystalAttributesComponent> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            GenerationProperties.CODEC.fieldOf("properties").forGetter(CrystalAttributesComponent::getProperties),
            TieredAttribute.CODEC.listOf().fieldOf("attributes").forGetter(CrystalAttributesComponent::getAttributes)
    ).apply(inst, CrystalAttributesComponent::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, CrystalAttributesComponent> STREAM_CODEC = StreamCodec.composite(
            GenerationProperties.STREAM_CODEC,
            CrystalAttributesComponent::getProperties,
            TieredAttribute.STREAM_CODEC.apply(ByteBufCodecs.list()),
            CrystalAttributesComponent::getAttributes,
            CrystalAttributesComponent::new);

    private final GenerationProperties properties;
    private final List<TieredAttribute> attributes;

    public CrystalAttributesComponent(GenerationProperties properties, TieredAttribute... attributes) {
        this(properties, List.of(attributes));
    }

    public CrystalAttributesComponent(GenerationProperties properties, List<TieredAttribute> attributes) {
        this.properties = properties;
        this.attributes = Collections.unmodifiableList(attributes);
    }

    public static CrystalAttributesComponent defaultEmpty() {
        return empty(0, 0);
    }

    public static CrystalAttributesComponent empty(int generateCount, int maxTierCount) {
        return new CrystalAttributesComponent(new GenerationProperties(generateCount, maxTierCount), new ArrayList<>());
    }

    public GenerationProperties getProperties() {
        return this.properties;
    }

    public CrystalAttributesComponent setProperties(GenerationProperties properties) {
        return new CrystalAttributesComponent(properties, List.copyOf(this.attributes));
    }

    public List<TieredAttribute> getAttributes() {
        return this.attributes;
    }

    public int getTotalTierCount() {
        return this.attributes.stream()
                .mapToInt(TieredAttribute::getTier)
                .sum();
    }

    public Optional<TieredAttribute> getAttribute(CrystalProperty property) {
        return this.attributes.stream()
                .filter(attr -> attr.getProperty() == property)
                .findFirst();
    }

    public int getAttributeTier(Supplier<? extends CrystalProperty> property) {
        return this.getAttributeTier(property.get());
    }

    public int getAttributeTier(TieredAttribute attribute) {
        return this.getAttributeTier(attribute.getProperty());
    }

    public int getAttributeTier(CrystalProperty property) {
        return this.getAttribute(property).map(TieredAttribute::getTier).orElse(0);
    }

    public CrystalAttributesComponent setAttributeTier(Supplier<? extends CrystalProperty> property, int tier) {
        return this.setAttributeTier(property.get(), tier);
    }

    public CrystalAttributesComponent setAttributeTier(TieredAttribute attribute, int tier) {
        return this.setAttributeTier(attribute.getProperty(), tier);
    }

    public CrystalAttributesComponent setAttributeTier(CrystalProperty property, int tier) {
        List<TieredAttribute> newAttributes = new ArrayList<>();
        boolean found = false;
        for (TieredAttribute attr : this.attributes) {
            if (attr.getProperty() == property) {
                found = true;
                if (tier > 0) {
                    newAttributes.add(new TieredAttribute(property, tier));
                }
            } else {
                newAttributes.add(attr);
            }
        }
        if (!found && tier > 0) {
            newAttributes.add(new TieredAttribute(property, tier));
        }
        return new CrystalAttributesComponent(this.getProperties(), List.copyOf(newAttributes));
    }

    @Override
    public void addTooltips(ItemStack stack, Consumer<Component> tooltipAdder, AttributeTooltipContext context) {
        for (CrystalProperty property : RegistriesAS.REGISTRY_CRYSTAL_PROPERTIES) {
            this.getAttribute(property).ifPresent(attribute -> {
                MutableComponent name = attribute.getProperty().getName(attribute.getTier())
                        .withStyle(attribute.getProperty().getColorStyle());
                MutableComponent tierCmp = Component.translatable(String.format("enchantment.level.%s", attribute.getTier()))
                        .withStyle(ChatFormatting.GOLD);
                tooltipAdder.accept(Component.translatable(attribute.getProperty().getNameFormat(), name, tierCmp));
            });
        }
    }

    public boolean isEmpty() {
        return this.attributes.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CrystalAttributesComponent that = (CrystalAttributesComponent) o;
        return Objects.equals(properties, that.properties) && Objects.equals(attributes, that.attributes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(properties, attributes);
    }

    public static class TieredAttribute {

        public static final Codec<TieredAttribute> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                RegistriesAS.REGISTRY_CRYSTAL_PROPERTIES.byNameCodec().fieldOf("property").forGetter(TieredAttribute::getProperty),
                Codec.INT.fieldOf("tier").forGetter(TieredAttribute::getTier)
        ).apply(inst, TieredAttribute::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, TieredAttribute> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.registry(RegistriesAS.KEY_CRYSTAL_PROPERTIES),
                TieredAttribute::getProperty,
                ByteBufCodecs.INT,
                TieredAttribute::getTier,
                TieredAttribute::new
        );

        private final CrystalProperty property;
        private final int tier;

        private TieredAttribute(CrystalProperty property, int tier) {
            this.property = property;
            this.tier = tier;
        }

        public CrystalProperty getProperty() {
            return this.property;
        }

        public int getTier() {
            return this.tier;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            TieredAttribute that = (TieredAttribute) o;
            return tier == that.tier && Objects.equals(property, that.property);
        }

        @Override
        public int hashCode() {
            return Objects.hash(tier, property);
        }
    }

    public record GenerationProperties(int generateCount, int maxTierCount) {

        public static final Codec<GenerationProperties> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                Codec.INT.fieldOf("generate_count").forGetter(GenerationProperties::generateCount),
                Codec.INT.fieldOf("max_tier_count").forGetter(GenerationProperties::maxTierCount)
        ).apply(inst, GenerationProperties::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, GenerationProperties> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.INT,
                GenerationProperties::generateCount,
                ByteBufCodecs.INT,
                GenerationProperties::maxTierCount,
                GenerationProperties::new
        );

    }
}
