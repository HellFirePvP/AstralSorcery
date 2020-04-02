/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.perk.modifier.DynamicAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.perk.source.AttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.source.provider.equipment.EquipmentAttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.LogicalSide;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: DynamicModifierHelper
 * Created by HellFirePvP
 * Date: 02.04.2020 / 19:57
 */
public class DynamicModifierHelper {

    public static final String KEY_MODIFIERS = "attribute_modifiers";

    public static void addModifier(ItemStack stack, UUID uuid, PerkAttributeType type, ModifierType mode, float value) {
        addModifier(stack, new DynamicAttributeModifier(uuid, type, mode, value));
    }

    public static void addModifier(ItemStack stack, DynamicAttributeModifier modifier) {
        addModifiers(stack, Collections.singletonList(modifier));
    }

    public static void addModifiers(ItemStack stack, Iterable<DynamicAttributeModifier> modifiers) {
        CompoundNBT tag = NBTHelper.getPersistentData(stack);
        ListNBT modifierList = tag.getList(KEY_MODIFIERS, Constants.NBT.TAG_COMPOUND);
        modifiers.forEach(modifier -> modifierList.add(modifier.serialize()));
        tag.put(KEY_MODIFIERS, modifierList);
    }

    public static List<PerkAttributeModifier> getDynamicModifiers(ItemStack stack, PlayerEntity player, LogicalSide side, boolean ignoreRequirements) {
        List<PerkAttributeModifier> modifiers = Lists.newArrayList();
        if (stack.getItem() instanceof AttributeModifierProvider) {
            modifiers.addAll(((AttributeModifierProvider) stack.getItem()).getModifiers(player, side, ignoreRequirements));
        }
        if (stack.getItem() instanceof EquipmentAttributeModifierProvider) {
            modifiers.addAll(((EquipmentAttributeModifierProvider) stack.getItem()).getModifiers(stack.copy(), player, side, ignoreRequirements));
        }
        modifiers.addAll(getStaticModifiers(stack));
        return modifiers;
    }

    public static List<DynamicAttributeModifier> getStaticModifiers(ItemStack stack) {
        List<DynamicAttributeModifier> modifiers = Lists.newArrayList();
        if (NBTHelper.hasPersistentData(stack)) {
            CompoundNBT tag = NBTHelper.getPersistentData(stack);
            ListNBT modifierList = tag.getList(KEY_MODIFIERS, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < modifierList.size(); i++) {
                CompoundNBT modifierTag = modifierList.getCompound(i);
                modifiers.add(DynamicAttributeModifier.deserialize(modifierTag));
            }
        }
        return modifiers;
    }
    @OnlyIn(Dist.CLIENT)
    public static void addModifierTooltip(ItemStack stack, List<ITextComponent> tooltip) {
        PlayerEntity clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null) {
            return;
        }

        for (PerkAttributeModifier mod : DynamicModifierHelper.getDynamicModifiers(stack, Minecraft.getInstance().player, LogicalSide.CLIENT, false)) {
            if (mod.hasDisplayString()) {
                tooltip.add(new StringTextComponent(mod.getLocalizedDisplayString())
                        .setStyle(new Style().setColor(TextFormatting.GRAY).setItalic(true)));
            }
        }
    }

}
