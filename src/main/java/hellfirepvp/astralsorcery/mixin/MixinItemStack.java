/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2022
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: MixinItemStack
 * Created by HellFirePvP
 * Date: 01.01.2022 / 09:52
 */
@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(method = "isEnchanted", at = @At("HEAD"), cancellable = true)
    public void addPrismEnchantmentGlint(CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = (ItemStack)(Object) this;
        if (!EnchantmentHelper.getEnchantments(stack).isEmpty()) {
            cir.setReturnValue(true);
        }
    }
}
