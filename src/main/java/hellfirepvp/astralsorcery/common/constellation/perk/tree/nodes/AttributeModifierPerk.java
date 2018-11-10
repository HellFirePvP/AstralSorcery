/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.perk.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.constellation.perk.PlayerAttributeMap;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.*;

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

    @Nullable
    public <T extends PerkAttributeModifier> T addModifier(float modifier, PerkAttributeModifier.Mode mode, String type) {
        PerkAttributeType attrType = AttributeTypeRegistry.getType(type);
        if (attrType != null) {
            PerkAttributeModifier mod = attrType.createModifier(modifier, mode);
            typeModifierList.add(mod);
            return (T) mod;
        }
        return null;
    }

    @Override
    public void applyPerkLogic(EntityPlayer player, Side side) {
        super.applyPerkLogic(player, side);

        PlayerAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, side);
        for (PerkAttributeModifier modifier : typeModifierList) {
            List<PerkAttributeModifier> modify = Lists.newArrayList();
            modify.add(modifier);
            modify.addAll(attr.gainModifiers(modifier, this));
            for (PerkAttributeModifier mod : modify) {
                mod = attr.convertModifier(mod, this);
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

        PlayerAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, side);
        for (PerkAttributeModifier modifier : typeModifierList) {
            List<PerkAttributeModifier> modify = Lists.newArrayList();
            modify.add(modifier);
            modify.addAll(attr.gainModifiers(modifier, this));
            for (PerkAttributeModifier mod : modify) {
                mod = attr.convertModifier(mod, this);
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
        boolean addEmptyLine = !this.typeModifierList.isEmpty();

        for (PerkAttributeModifier modifier : this.typeModifierList) {
            String modifierDisplay = modifier.getLocalizedDisplayString();
            if (modifierDisplay != null) {
                tooltip.add(modifierDisplay);
            } else {
                addEmptyLine = false;
            }
        }

        return addEmptyLine;
    }
}
