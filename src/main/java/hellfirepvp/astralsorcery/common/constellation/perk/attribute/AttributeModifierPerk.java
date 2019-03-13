/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.PlayerAttributeMap;
import hellfirepvp.astralsorcery.common.data.research.PlayerProgress;
import hellfirepvp.astralsorcery.common.data.research.ResearchManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
 * Date: 09.07.2018 / 15:10
 */
//Usable for most/many cases. Handles also all basic stuff around modifiers and converters
public class AttributeModifierPerk extends AttributeConverterPerk {

    private List<PerkAttributeModifier> typeModifierList = Lists.newArrayList();

    public AttributeModifierPerk(String name, int x, int y) {
        super(name, x, y);
    }

    public AttributeModifierPerk(ResourceLocation name, int x, int y) {
        super(name, x, y);
    }

    @Nullable
    public <T extends PerkAttributeModifier> T addModifier(float modifier, PerkAttributeModifier.Mode mode, String type) {
        PerkAttributeType attrType = AttributeTypeRegistry.getType(type);
        if (attrType != null) {
            return addModifier((T) attrType.createModifier(modifier, mode));
        }
        return null;
    }

    @Nullable
    protected <T extends PerkAttributeModifier> T addModifier(T modifier) {
        typeModifierList.add(modifier);
        return modifier;
    }

    @Override
    protected void applyEffectMultiplier(double multiplier) {
        super.applyEffectMultiplier(multiplier);

        typeModifierList.forEach(t -> t.multiplyValue(multiplier));
    }

    protected Collection<PerkAttributeModifier> getModifiers(EntityPlayer player, Side side) {
        if (modifiersDisabled(player, side)) {
            return Collections.emptyList();
        }

        return new ArrayList<>(this.typeModifierList);
    }

    @Override
    public void applyPerkLogic(EntityPlayer player, Side side) {
        super.applyPerkLogic(player, side);

        PlayerProgress prog = ResearchManager.getProgress(player, side);
        PlayerAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, side);
        for (PerkAttributeModifier modifier : getModifiers(player, side)) {
            List<PerkAttributeModifier> modify = Lists.newArrayList();
            modify.add(modifier);
            modify.addAll(attr.gainModifiers(player, prog, modifier, this));
            for (PerkAttributeModifier mod : modify) {
                mod = attr.convertModifier(player, prog, mod, this);
                if(!attr.applyModifier(player, mod.getAttributeType(), mod)) {
                    //For testing if application/removal of perks goes wrong, set a debug breakpoint here.
                    //System.out.println("FAILED TO ADD MODIFIER! ALREADY PRESENT!");
                }
            }
        }
    }

    @Override
    public void removePerkLogic(EntityPlayer player, Side side) {
        super.removePerkLogic(player, side);

        PlayerProgress prog = ResearchManager.getProgress(player, side);
        PlayerAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, side);
        for (PerkAttributeModifier modifier : getModifiers(player, side)) {
            List<PerkAttributeModifier> modify = Lists.newArrayList();
            modify.add(modifier);
            modify.addAll(attr.gainModifiers(player, prog, modifier, this));
            for (PerkAttributeModifier mod : modify) {
                mod = attr.convertModifier(player, prog, mod, this);
                if(!attr.removeModifier(player, mod.getAttributeType(), mod)) {
                    //For testing if application/removal of perks goes wrong, set a debug breakpoint here.
                    //System.out.println("FAILED TO REMOVE MODIFIER! NOT FOUND!");
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean addLocalizedTooltip(Collection<String> tooltip) {
        Collection<PerkAttributeModifier> modifiers = this.getModifiers(Minecraft.getMinecraft().player, Side.CLIENT);
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
