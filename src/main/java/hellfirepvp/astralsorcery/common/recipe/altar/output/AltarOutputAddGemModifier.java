/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.recipe.altar.output;

import com.mojang.serialization.MapCodec;
import hellfirepvp.astralsorcery.common.component.DynamicModifiersComponent;
import hellfirepvp.astralsorcery.common.lib.DataComponentsAS;
import hellfirepvp.astralsorcery.common.perk.DynamismGemModifierHelper;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.recipe.altar.AltarCraftingInput;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AltarOutputAddGemModifier
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AltarOutputAddGemModifier extends AltarRecipeOutputModifier {

    public static final AltarOutputAddGemModifier INSTANCE = new AltarOutputAddGemModifier();
    public static final MapCodec<AltarOutputAddGemModifier> CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, AltarOutputAddGemModifier> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final Type<AltarOutputAddGemModifier> TYPE = new Type<>(CODEC, STREAM_CODEC);

    private AltarOutputAddGemModifier() {}

    @Override
    public ItemStack modifyOutput(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
       DynamicModifiersComponent modifiers = output.get(DataComponentsAS.DYNAMIC_MODIFIERS);
       if (modifiers != null) {
           List<DynamicAttributeModifier> mods = new ArrayList<>(modifiers.modifiers());
           int attempts = 50;
           DynamicAttributeModifier newMod;
           do {
               attempts--;
               newMod = DynamismGemModifierHelper.generateModifier(mods, RandomSource.create(), 3F);
           } while (newMod == null && attempts > 0);
           if (newMod != null) {
                mods.add(newMod);
                output.set(DataComponentsAS.DYNAMIC_MODIFIERS, new DynamicModifiersComponent(mods));
           }
       }
        return output;
    }

    @Override
    public ItemStack modifyOutputForDisplay(ItemStack output, AltarCraftingInput input, HolderLookup.Provider registries) {
        ItemLore lore = output.getOrDefault(DataComponents.LORE, ItemLore.EMPTY);
        lore = lore.withLineAdded(Component.translatable("recipe.astralsorcery.result.add_gem_modifier").withStyle(ChatFormatting.GRAY));
        output.set(DataComponents.LORE, lore);
        return output;
    }

    @Override
    public Type<?> getType() {
        return TYPE;
    }
}
