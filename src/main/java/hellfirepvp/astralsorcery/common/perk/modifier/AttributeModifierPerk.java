/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.modifier;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import hellfirepvp.astralsorcery.common.perk.AttributeConverterPerk;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.perk.PerkAttributeMap;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import hellfirepvp.astralsorcery.common.util.log.LogCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeModifierPerk
 * Created by HellFirePvP
 * Date: 08.08.2019 / 18:11
 */
//Usable for most/many cases. Handles also all basic stuff around modifiers and converters
public class AttributeModifierPerk extends AttributeConverterPerk {

    private List<PerkAttributeModifier> typeModifierList = Lists.newArrayList();

    public AttributeModifierPerk(ResourceLocation name, int x, int y) {
        super(name, x, y);
    }

    @Nonnull
    public <T extends PerkAttributeModifier> T addModifier(float modifier, ModifierType mode, PerkAttributeType type) {
        return (T) this.addModifier(type.createModifier(modifier, mode));
    }

    @Nonnull
    protected <T extends PerkAttributeModifier> T addModifier(T modifier) {
        typeModifierList.add(modifier);
        return modifier;
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        typeModifierList.forEach(t -> t.multiplyValue(multiplier));
    }

    protected Collection<PerkAttributeModifier> getModifiers(PlayerEntity player, LogicalSide side) {
        if (modifiersDisabled(player, side)) {
            return Collections.emptyList();
        }

        return new ArrayList<>(this.typeModifierList);
    }

    @Override
    public void applyPerkLogic(PlayerEntity player, LogicalSide side) {
        super.applyPerkLogic(player, side);

        LogCategory.PERKS.info(() -> "Applying modifiers of " + this.getRegistryName() + " on " + side.name());

        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        PerkAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, side);
        for (PerkAttributeModifier modifier : getModifiers(player, side)) {
            List<PerkAttributeModifier> modify = Lists.newArrayList();
            modify.add(modifier);
            modify.addAll(attr.gainModifiers(player, prog, modifier, this));
            for (PerkAttributeModifier mod : modify) {

                PerkAttributeModifier preMod = mod;
                LogCategory.PERKS.info(() -> "Applying unique modifier " + preMod.getId());

                mod = attr.convertModifier(player, prog, mod, this);

                PerkAttributeModifier postMod = mod;
                LogCategory.PERKS.info(() -> "Applying converted modifier " + postMod.getId());
                if(!attr.applyModifier(player, mod.getAttributeType(), mod)) {
                    LogCategory.PERKS.warn(() -> "Could not apply modifier " + postMod.getId() + " - already applied!");
                }
            }
        }
    }

    @Override
    public void removePerkLogic(PlayerEntity player, LogicalSide side) {
        super.removePerkLogic(player, side);

        LogCategory.PERKS.info(() -> "Removing modifiers of " + this.getRegistryName() + " on " + side.name());

        PlayerProgress prog = ResearchHelper.getProgress(player, side);
        PerkAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, side);
        for (PerkAttributeModifier modifier : getModifiers(player, side)) {
            List<PerkAttributeModifier> modify = Lists.newArrayList();
            modify.add(modifier);
            modify.addAll(attr.gainModifiers(player, prog, modifier, this));
            for (PerkAttributeModifier mod : modify) {

                PerkAttributeModifier preMod = mod;
                LogCategory.PERKS.info(() -> "Removing unique modifier " + preMod.getId());

                mod = attr.convertModifier(player, prog, mod, this);

                PerkAttributeModifier postMod = mod;
                LogCategory.PERKS.info(() -> "Removing converted modifier " + postMod.getId());
                if(!attr.removeModifier(player, mod.getAttributeType(), mod)) {
                    LogCategory.PERKS.warn(() -> "Could not remove modifier " + postMod.getId() + " - not applied!");
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addLocalizedTooltip(Collection<String> tooltip) {
        Collection<PerkAttributeModifier> modifiers = this.getModifiers(Minecraft.getInstance().player, LogicalSide.CLIENT);
        boolean addEmptyLine = !modifiers.isEmpty();

        if (canSeeClient()) {
            for (PerkAttributeModifier modifier : modifiers) {
                String modifierDisplay = modifier.getLocalizedDisplayString();
                if (modifierDisplay != null) {
                    tooltip.add(modifierDisplay);
                } else {
                    addEmptyLine = false;
                }
            }
        }

        return addEmptyLine;
    }
}

