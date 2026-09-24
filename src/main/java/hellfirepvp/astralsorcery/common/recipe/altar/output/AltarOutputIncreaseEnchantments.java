/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.output;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputIncreaseEnchantments
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarOutputIncreaseEnchantments extends AltarRecipeOutputModifier {

    public static final MapCodec<AltarOutputIncreaseEnchantments> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Codec.FLOAT.fieldOf("additional_level_chance").forGetter(AltarOutputIncreaseEnchantments::getAdditionalLevelChance)
    ).apply(inst, AltarOutputIncreaseEnchantments::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AltarOutputIncreaseEnchantments> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT,
            AltarOutputIncreaseEnchantments::getAdditionalLevelChance,
            AltarOutputIncreaseEnchantments::new);
    public static final Type<AltarOutputIncreaseEnchantments> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private final float additionalLevelChance;

    public AltarOutputIncreaseEnchantments(float additionalLevelChance) {
        this.additionalLevelChance = additionalLevelChance;
    }

    public static AltarOutputIncreaseEnchantments of() {
        return of(0F);
    }

    public static AltarOutputIncreaseEnchantments of(float additionalLevelChance) {
        return new AltarOutputIncreaseEnchantments(additionalLevelChance);
    }

    public float getAdditionalLevelChance() {
        return this.additionalLevelChance;
    }

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        if (output.isEnchanted()) {
            EnchantmentHelper.updateEnchantments(output, enchantments -> {
                RandomSource rand = RandomSource.create();
                enchantments.keySet().forEach(ench -> {
                    if (ench.is(EnchantmentTags.CURSE)) return;
                    if (ench.value().getMaxLevel() == 1) return;

                    int newLevel = enchantments.getLevel(ench) + 1;
                    while (rand.nextFloat() < this.additionalLevelChance) newLevel++;
                    enchantments.upgrade(ench, newLevel);
                });
            });
        }
        return output;
    }

    @Override
    public ItemStack modifyOutputForDisplay(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        ItemLore lore = output.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        lore = lore.withLineAdded(Component.translatable("recipe.astralsorcery.result.increase_enchantment").withStyle(ChatFormatting.GRAY));
        output.set(DataComponents.LORE, lore);
        return output;
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }
}
