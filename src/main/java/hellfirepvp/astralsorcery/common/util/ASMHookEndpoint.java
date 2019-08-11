/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
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
        return enchantments;
    }

    public static int getNewEnchantmentLevel(int current, Enchantment enchantment, ItemStack stack) {
        return current;
    }

    public static ListNBT addNewEnchantmentLevelsTag(ListNBT list, ItemStack stack) {
        return list;
    }

    public static void addNoTagTooltip(ItemStack stack, List<ITextComponent> tooltip) {

    }

    public static AbstractAttributeMap markPlayer(AbstractAttributeMap map, LivingEntity entity) {
        AttributeEvent.setEntity(map, entity);
        return map;
    }

    public static double postProcessVanilla(double value, ModifiableAttributeInstance attributeInstance) {
        AttributeEvent.PostProcessVanilla event = new AttributeEvent.PostProcessVanilla(attributeInstance, value);
        MinecraftForge.EVENT_BUS.post(event);
        return event.getAttribute().clampValue(event.getValue());
    }

}
