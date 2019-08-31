/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantment;
import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantmentHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.event.handler.EventHandlerCache;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ASMHookEndpoint
 * Created by HellFirePvP
 * Date: 08.08.2019 / 06:52
 */
public class ASMHookEndpoint {

    public static Map<Enchantment, Integer> applyNewEnchantmentLevels(Map<Enchantment, Integer> enchantments, ItemStack stack) {
        return DynamicEnchantmentHelper.addNewLevels(enchantments, stack);
    }

    public static int getNewEnchantmentLevel(int current, Enchantment enchantment, ItemStack stack) {
        return DynamicEnchantmentHelper.getNewEnchantmentLevel(current, enchantment, stack, null);
    }

    public static ListNBT addNewEnchantmentLevelsTag(ListNBT list, ItemStack stack) {
        return DynamicEnchantmentHelper.modifyEnchantmentTags(list, stack);
    }

    @OnlyIn(Dist.CLIENT)
    public static void addNoTagTooltip(ItemStack stack, List<ITextComponent> tooltip) {
        Map<Enchantment, Integer> enchantments;
        if (!stack.hasTag() && !(enchantments = EnchantmentHelper.getEnchantments(stack)).isEmpty()) {
            for (Enchantment e : enchantments.keySet()) {
                tooltip.add(e.getDisplayName(enchantments.get(e)));
            }
        }
    }

    public static AbstractAttributeMap markPlayer(AbstractAttributeMap map, LivingEntity entity) {
        AttributeEvent.setEntity(map, entity);
        return map;
    }

    public static double postProcessVanilla(double value, ModifiableAttributeInstance attributeInstance) {
        return AttributeEvent.postProcessVanilla(value, attributeInstance);
    }

    public static void onClientDisconnect() {
        EventHandlerCache.onClientDisconnect();
    }

}
