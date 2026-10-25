/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.mixin;

import hellfirepvp.astralsorcery.common.event.helper.EnchantmentModifierHelper;
import hellfirepvp.astralsorcery.common.event.helper.VeiledItemStackHelper;
import hellfirepvp.astralsorcery.common.item.base.ItemStackEquipmentReference;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MixinItemStack
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
@Mixin(ItemStack.class)
public class MixinItemStack implements ItemStackEquipmentReference {

    @Unique
    @Nullable
    private UUID astralSorcery_1_21$playerReference;

    @Unique
    @Nullable
    private EquipmentSlot astralSorcery_1_21$equippedSlot;

    @Nullable
    @Override
    public UUID getPlayerReference() {
        return this.astralSorcery_1_21$playerReference;
    }

    @Override
    public void setPlayerReference(@Nullable UUID playerReference) {
        if ((Object) this == ItemStack.EMPTY) return;
        this.astralSorcery_1_21$playerReference = playerReference;
    }

    @Nullable
    @Override
    public EquipmentSlot getEquippedSlot() {
        return this.astralSorcery_1_21$equippedSlot;
    }

    @Override
    public void setEquippedSlot(@Nullable EquipmentSlot slot) {
        if ((Object) this == ItemStack.EMPTY) return;
        this.astralSorcery_1_21$equippedSlot = slot;
    }

    @Inject(method = { "copy", "transmuteCopyIgnoreEmpty" }, at = @At("RETURN"))
    public void copyPlayerReference(CallbackInfoReturnable<ItemStack> cir) {
        ItemStackEquipmentReference.setEquipmentReference(cir.getReturnValue(),
                this.astralSorcery_1_21$playerReference,
                this.astralSorcery_1_21$equippedSlot);
    }

    @Inject(method = "getHoverName", at = @At("HEAD"), cancellable = true, order = 800)
    public void getVeiledHoverName(CallbackInfoReturnable<Component> cir) {
        ItemStack thisStack = MiscUtil.cast(this);
        ItemStack displayed = VeiledItemStackHelper.getDisplayedStack(thisStack);
        if (displayed != thisStack) {
            cir.setReturnValue(VeiledItemStackHelper.markUnknownVeil(thisStack, displayed.getHoverName()));
        }
    }

    @ModifyVariable(method = "addToTooltip", at = @At(value = "STORE", target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"))
    public TooltipProvider addEnchantmentTooltip(TooltipProvider value) {
        if (value instanceof ItemEnchantments enchantments) {
            return EnchantmentModifierHelper.addEnchantments(MiscUtil.cast(this), enchantments);
        }
        return value;
    }
}
