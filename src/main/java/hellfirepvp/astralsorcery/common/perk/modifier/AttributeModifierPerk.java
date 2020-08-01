/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.modifier;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.data.research.ResearchHelper;
import hellfirepvp.astralsorcery.common.perk.AttributeConverterPerk;
import hellfirepvp.astralsorcery.common.perk.source.AttributeModifierProvider;
import hellfirepvp.astralsorcery.common.perk.type.ModifierType;
import hellfirepvp.astralsorcery.common.perk.type.PerkAttributeType;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;

import javax.annotation.Nonnull;
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
public class AttributeModifierPerk extends AttributeConverterPerk implements AttributeModifierProvider {

    private List<PerkAttributeModifier> typeModifierList = Lists.newArrayList();

    public AttributeModifierPerk(ResourceLocation name, int x, int y) {
        super(name, x, y);
    }

    @Nonnull
    public <V extends AttributeModifierPerk> V addModifier(float modifier, ModifierType mode, PerkAttributeType type) {
        return (V) this.addModifier(type.createModifier(modifier, mode));
    }

    @Nonnull
    protected <T extends PerkAttributeModifier, V extends AttributeModifierPerk> V addModifier(T modifier) {
        typeModifierList.add(modifier);
        return (V) this;
    }

    @Override
    protected void applyEffectMultiplier(float multiplier) {
        super.applyEffectMultiplier(multiplier);

        typeModifierList.forEach(t -> t.multiplyValue(multiplier));
    }

    @Override
    public Collection<PerkAttributeModifier> getModifiers(PlayerEntity player, LogicalSide side, boolean ignoreRequirements) {
        if (modifiersDisabled(player, side)) {
            return Collections.emptyList();
        }
        if (!ignoreRequirements && ResearchHelper.getProgress(player, side).isPerkSealed(this)) {
            return Collections.emptyList();
        }

        return new ArrayList<>(this.typeModifierList);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean addLocalizedTooltip(Collection<ITextComponent> tooltip) {
        Collection<PerkAttributeModifier> modifiers = this.getModifiers(Minecraft.getInstance().player, LogicalSide.CLIENT, true);
        boolean addEmptyLine = !modifiers.isEmpty();

        if (canSeeClient()) {
            for (PerkAttributeModifier modifier : modifiers) {
                String modifierDisplay = modifier.getLocalizedDisplayString();
                if (modifierDisplay != null) {
                    tooltip.add(new StringTextComponent(modifierDisplay));
                } else {
                    addEmptyLine = false;
                }
            }
        }

        return addEmptyLine;
    }
}

