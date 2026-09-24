/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.event.helper.EnchantmentModifierHelper;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinItemStack
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(ItemStack.class)
public class MixinItemStack {

    @ModifyVariable(method = "addToTooltip", at = @At(value = "STORE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    public TooltipProvider addEnchantmentTooltip(TooltipProvider value) {
        if (value instanceof ItemEnchantments enchantments) {
            return EnchantmentModifierHelper.addEnchantments(MiscUtil.cast(this), enchantments);
        }
        return value;
    }
}
