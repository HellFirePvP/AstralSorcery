/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin.client;

import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantmentHelper;
import hellfirepvp.astralsorcery.common.perk.DynamicModifierHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinItemStack
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasTag()Z", ordinal = 0), locals = LocalCapture.CAPTURE_FAILSOFT)
    public void addMissingEnchantmentTooltip(PlayerEntity player, ITooltipFlag advanced, CallbackInfoReturnable<List<ITextComponent>> cir, List<ITextComponent> tooltip) {
        ItemStack stack = (ItemStack)(Object) this;

        List<ITextComponent> addition = new ArrayList<>();
        try {
            //Add any dynamic modifiers this item has.
            DynamicModifierHelper.addModifierTooltip(stack, addition);

            //Add prism enchantments
            Map<Enchantment, Integer> enchantments;
            if (!stack.hasTag() && !(enchantments = EnchantmentHelper.getEnchantments(stack)).isEmpty()) {
                for (Enchantment e : enchantments.keySet()) {
                    addition.add(e.getDisplayName(enchantments.get(e)));
                }
            }
        } catch (Exception exc) {
            addition.clear();
            tooltip.add(new TranslationTextComponent("astralsorcery.misc.tooltipError").mergeStyle(TextFormatting.GRAY));
        }
        tooltip.addAll(addition);
    }

    @Redirect(method = "getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getEnchantmentTagList()Lnet/minecraft/nbt/ListNBT;"))
    public ListNBT enhanceEnchantmentTooltip(ItemStack stack) {
        return DynamicEnchantmentHelper.modifyEnchantmentTags(stack.getEnchantmentTagList(), stack);
    }
}
