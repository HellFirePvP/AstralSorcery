/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.output;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.component.CrystalAttributesComponent;
import hellfirepvp.astralsorcery.common.crystal.CrystalProperty;
import hellfirepvp.astralsorcery.common.event.CrystalPropertyEvent;
import hellfirepvp.astralsorcery.common.item.tool.CrystalToolItem;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import hellfirepvp.astralsorcery.common.util.codec.CodecUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.neoforged.neoforge.common.NeoForge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputEngraveLumen
 * Created by HellFirePvP
 * Date: 19.10.2026 / 19:32
 */
public class AltarOutputEngraveLumen extends AltarRecipeOutputModifier {

    public static final MapCodec<AltarOutputEngraveLumen> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            WeightedEntry.Wrapper.codec(RegistriesAS.REGISTRY_CRYSTAL_PROPERTIES.byNameCodec()).listOf()
                    .fieldOf("properties").forGetter(AltarOutputEngraveLumen::getProperties)
    ).apply(inst, AltarOutputEngraveLumen::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarOutputEngraveLumen> STREAM_CODEC = StreamCodec.composite(
            CodecUtil.weightedEntryCodec(ByteBufCodecs.registry(RegistriesAS.KEY_CRYSTAL_PROPERTIES)).apply(ByteBufCodecs.list()), AltarOutputEngraveLumen::getProperties,
            AltarOutputEngraveLumen::new
    );
    public static final Type<AltarOutputEngraveLumen> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private final List<WeightedEntry.Wrapper<CrystalProperty>> properties;

    public AltarOutputEngraveLumen(List<WeightedEntry.Wrapper<CrystalProperty>> properties) {
        this.properties = List.copyOf(properties);
    }

    public static AltarOutputEngraveLumen of(WeightedEntry.Wrapper<CrystalProperty>... properties) {
        return new AltarOutputEngraveLumen(Arrays.asList(properties));
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<WeightedEntry.Wrapper<CrystalProperty>> getProperties() {
        return this.properties;
    }

    @Override
    public ItemStack modifyOutputForDisplay(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries, long tick) {
        ItemLore lore = output.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        lore = lore.withLineAdded(Component.translatable("ingredient.astralsorcery.lumen_engraving.description").withStyle(ChatFormatting.RESET).withStyle(ChatFormatting.GRAY));
        for (WeightedEntry.Wrapper<CrystalProperty> property : this.getProperties()) {
            lore = lore.withLineAdded(Component.translatable("ingredient.astralsorcery.lumen_engraving.description.element",
                            property.data().getName(1, tick))
                    .withStyle(ChatFormatting.GRAY));
        }
        output.set(DataComponents.LORE, lore);
        return output;
    }

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        CrystalAttributesComponent cmp = output.getOrDefault(DataComponentsAS.CRYSTAL_ATTRIBUTES, CrystalAttributesComponent.defaultEmpty());
        if (cmp.getTotalTierCount() >= cmp.getProperties().maxTierCount()) return output;

        RandomSource rand = RandomSource.create();
        CrystalProperty prop = WeightedRandom.getRandomItem(rand, this.getProperties()).map(WeightedEntry.Wrapper::data).orElse(null);
        if (prop != null) {
            int tier = cmp.getAttributeTier(prop);
            if (tier < prop.getMaxTier()) {
                CrystalAttributesComponent prev = cmp;
                cmp = cmp.setAttributeTier(prop, tier + 1);

                CrystalPropertyEvent.Change engraved = new CrystalPropertyEvent.Change(output.copy(),
                        CrystalPropertyEvent.Change.Type.ENGRAVE_PROPERTY, prev, cmp);
                NeoForge.EVENT_BUS.post(engraved);

                output.set(DataComponentsAS.CRYSTAL_ATTRIBUTES, engraved.getResultComponent());
                CrystalToolItem.updateModifiers(output);
            }
        }
        return output;
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }

    public static class Builder {

        private final List<WeightedEntry.Wrapper<CrystalProperty>> properties = new ArrayList<>();

        private Builder() {}

        public Builder add(Supplier<? extends CrystalProperty> property, int weight) {
            return this.add(property.get(), Weight.of(weight));
        }

        public Builder add(CrystalProperty property, Weight weight) {
            this.properties.add(new WeightedEntry.Wrapper<>(property, weight));
            return this;
        }

        public AltarOutputEngraveLumen build() {
            return new AltarOutputEngraveLumen(this.properties);
        }
    }
}
