/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.listener;

import hellfirepvp.astralsorcery.common.auxiliary.SwordSharpenHelper;
import hellfirepvp.astralsorcery.common.data.config.Config;
import hellfirepvp.astralsorcery.common.event.ItemEnchantmentTooltipEvent;
import hellfirepvp.astralsorcery.common.item.wearable.ItemEnchantmentAmulet;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerMisc
 * Created by HellFirePvP
 * Date: 04.11.2016 / 23:42
 */
public class EventHandlerMisc {

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onToolTip(ItemTooltipEvent event) {
        List<String> toolTip = event.getToolTip();
        ItemStack stack = event.getItemStack();

        if (stack.getItem() instanceof ItemEnchantmentAmulet &&
                ItemEnchantmentAmulet.getAmuletColor(stack).orElse(0) == 0xFFFFFFFF) {
            List<String> newTooltip = new LinkedList<>();
            if(toolTip.size() > 1) {
                newTooltip.addAll(toolTip);
                newTooltip.add(1, TextFormatting.GRAY.toString() + TextFormatting.ITALIC.toString() + I18n.format("item.itemenchantmentamulet.pure"));
            } else {
                newTooltip.add(TextFormatting.GRAY.toString() + TextFormatting.ITALIC.toString() + I18n.format("item.itemenchantmentamulet.pure"));
                newTooltip.addAll(toolTip);
            }

            toolTip.clear();
            toolTip.addAll(newTooltip);
        }

        if (SwordSharpenHelper.isSwordSharpened(stack)) {
            List<String> newTooltip = new LinkedList<>();
            if(toolTip.size() > 1) {
                newTooltip.addAll(toolTip);
                newTooltip.add(1, I18n.format("misc.sword.sharpened", String.valueOf(Math.round(Config.swordSharpMultiplier * 100)) + "%"));
            } else {
                newTooltip.add(I18n.format("misc.sword.sharpened", String.valueOf(Math.round(Config.swordSharpMultiplier * 100)) + "%"));
                newTooltip.addAll(toolTip);
            }
            toolTip.clear();
            toolTip.addAll(newTooltip);
        }
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onEnchTooltip(ItemEnchantmentTooltipEvent event) {
        List<String> toolTip = event.getToolTip();
        ItemStack stack = event.getItemStack();

        Map<Enchantment, Integer> enchantments;
        if(!stack.hasTagCompound() && !(enchantments = EnchantmentHelper.getEnchantments(stack)).isEmpty()) {
            for (Enchantment e : enchantments.keySet()) {
                toolTip.add(e.getTranslatedName(enchantments.get(e)));
            }
        }
    }

    //Player CAP stuffs.

    /*@SubscribeEvent
    public void onAttach(AttachCapabilitiesEvent<Entity> event) {
        if(event.getObject() instanceof EntityPlayer) {
            event.addCapability(new ResourceLocation(AstralSorcery.MODID, "constellationperks"), new IPlayerCapabilityPerks.Provider());
        }
    }

    @SubscribeEvent
    public void onClone(PlayerEvent.Clone event) {
        IPlayerCapabilityPerks current = PlayerPerkHelper.getPerks(event.getEntityPlayer());
        IPlayerCapabilityPerks cloned = PlayerPerkHelper.getPerks(event.getEntityPlayer());
        if(cloned != null && current != null) {
            cloned.updatePerks(current.getAttunedConstellation(), current.getCurrentPlayerPerks());
        }
    }*/

}
