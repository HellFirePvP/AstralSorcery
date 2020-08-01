/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util;

import hellfirepvp.astralsorcery.common.constellation.SkyHandler;
import hellfirepvp.astralsorcery.common.constellation.mantle.effect.MantleEffectOctans;
import hellfirepvp.astralsorcery.common.constellation.mantle.effect.MantleEffectVicio;
import hellfirepvp.astralsorcery.common.constellation.world.WorldContext;
import hellfirepvp.astralsorcery.common.enchantment.dynamic.DynamicEnchantmentHelper;
import hellfirepvp.astralsorcery.common.event.AttributeEvent;
import hellfirepvp.astralsorcery.common.event.CooldownSetEvent;
import hellfirepvp.astralsorcery.common.event.PotionApplyEvent;
import hellfirepvp.astralsorcery.common.perk.DynamicModifierHelper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.ServerCooldownTracker;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.LogicalSide;

import java.util.ArrayList;
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
    public static void addTooltipPreEnchantments(ItemStack stack, List<ITextComponent> tooltip) {
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
            tooltip.add(new TranslationTextComponent("astralsorcery.misc.tooltipError").applyTextStyle(TextFormatting.GRAY));
        }
        tooltip.addAll(addition);
    }

    @OnlyIn(Dist.CLIENT)
    public static float overrideSunBrightnessClient(float prevBrightness, World world) {
        WorldContext ctx = SkyHandler.getContext(world, LogicalSide.CLIENT);
        if (ctx != null && ctx.getCelestialHandler().isSolarEclipseActive()) {
            float perc = ctx.getCelestialHandler().getSolarEclipsePercent();
            perc = 0.05F + (perc * 0.95F);

            return prevBrightness * perc;
        }
        return prevBrightness;
    }

    public static int overrideSunBrightnessServer(int prevSkyLight, World world) {
        WorldContext ctx = SkyHandler.getContext(world);
        if (ctx != null && ctx.getCelestialHandler().isSolarEclipseActive()) {
            return 11 - Math.round(ctx.getCelestialHandler().getSolarEclipsePercent() * 11F);
        }
        return prevSkyLight;
    }

    public static ItemStack transformElytraItem(ItemStack elytraStack, LivingEntity wearingEntity) {
        if (!elytraStack.isEmpty() && wearingEntity instanceof PlayerEntity) {
            if (MantleEffectVicio.isUsableElytra(elytraStack, (PlayerEntity) wearingEntity)) {
                elytraStack = new ItemStack(Items.ELYTRA);
            }
        }
        return elytraStack;
    }

    public static void fireNewPotionEffectEvent(LivingEntity entity, EffectInstance newEffect) {
        MinecraftForge.EVENT_BUS.post(new PotionApplyEvent.New(entity, newEffect));
    }

    public static void fireChangedPotionEffectEvent(LivingEntity entity, EffectInstance previous, EffectInstance newCombinedEffect) {
        MinecraftForge.EVENT_BUS.post(new PotionApplyEvent.Changed(entity, previous, newCombinedEffect));
    }

    public static int fireCooldownEvent(CooldownTracker tracker, Item item, int ticks) {
        if (tracker instanceof ServerCooldownTracker) {
            CooldownSetEvent event = new CooldownSetEvent(((ServerCooldownTracker) tracker).player, item, ticks);
            MinecraftForge.EVENT_BUS.post(event);
            ticks = Math.max(event.getResultCooldown(), 1);
        }
        return ticks;
    }

    public static float getLivingEntityWaterSlowDown(float slowDownIn, LivingEntity entity) {
        if (!entity.getItemStackFromSlot(EquipmentSlotType.CHEST).isEmpty()) {
            if (MantleEffectOctans.shouldPreventWaterSlowdown(entity.getItemStackFromSlot(EquipmentSlotType.CHEST), entity)) {
                return 0.92F;
            }
        }
        return slowDownIn;
    }

    public static AbstractAttributeMap markPlayer(AbstractAttributeMap map, LivingEntity entity) {
        AttributeEvent.setEntity(map, entity);
        return map;
    }

    public static double postProcessVanilla(double value, ModifiableAttributeInstance attributeInstance) {
        return AttributeEvent.postProcessVanilla(value, attributeInstance);
    }

}
