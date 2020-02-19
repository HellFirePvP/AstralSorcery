/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.gem;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.perk.modifier.GemAttributeModifier;
import hellfirepvp.astralsorcery.common.registry.RegistryItems;
import hellfirepvp.astralsorcery.common.util.nbt.NBTHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ItemPerkGem
 * Created by HellFirePvP
 * Date: 09.08.2019 / 07:25
 */
public abstract class ItemPerkGem extends Item {

    private final GemType type;

    public ItemPerkGem(GemType type) {
        super(new Properties()
                .maxStackSize(1)
                .group(RegistryItems.ITEM_GROUP_AS));
        this.type = type;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        PlayerProgress prog = ResearchHelper.getClientProgress();
        if (prog.wasOnceAttuned()) {
            for (GemAttributeModifier mod : getModifiers(stack)) {
                if (mod.hasDisplayString()) {
                    tooltip.add(new StringTextComponent(mod.getLocalizedDisplayString())
                            .setStyle(new Style().setColor(TextFormatting.GRAY).setItalic(true)));
                }
            }
        } else {
            tooltip.add(new TranslationTextComponent("astralsorcery.progress.missing.knowledge")
                    .setStyle(new Style().setColor(TextFormatting.GRAY)));
        }
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        return new TranslationTextComponent(this.getTranslationKey(stack)).setStyle(new Style().setColor(this.getRarity(stack).color));
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entityIn, int itemSlot, boolean isSelected) {
        if (world.isRemote()) {
            return;
        }

        if (getModifiers(stack).isEmpty()) {
            GemAttributeHelper.rollGem(stack);
        }
    }

    @Nonnull
    public static List<GemAttributeModifier> getModifiers(ItemStack stack) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemPerkGem)) {
            return Lists.newArrayList();
        }
        List<GemAttributeModifier> modifiers = Lists.newArrayList();
        ListNBT mods = NBTHelper.getPersistentData(stack).getList("modifiers", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < mods.size(); i++) {
            CompoundNBT tag = mods.getCompound(i);
            modifiers.add(GemAttributeModifier.deserialize(tag));
        }
        return modifiers;
    }

    public static boolean setModifiers(ItemStack stack, List<GemAttributeModifier> modifiers) {
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemPerkGem)) {
            return false;
        }
        ListNBT mods = new ListNBT();
        for (GemAttributeModifier modifier : modifiers) {
            mods.add(modifier.serialize());
        }
        NBTHelper.getPersistentData(stack).put("modifiers", mods);
        return true;
    }

    @Nullable
    public static GemType getGemType(ItemStack gem) {
        if (gem.isEmpty() || !(gem.getItem() instanceof ItemPerkGem)) {
            return null;
        }
        return ((ItemPerkGem) gem.getItem()).type;
    }

}
