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
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;
import java.util.Stack;

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

    public <T> T addModifier(float modifier, PerkAttributeModifier.Mode mode, String type) {
        typeModifierList.add(new PerkAttributeModifier(type, mode, modifier));
        return (T) this;
    }

    @Override
    public void applyPerkLogic(EntityPlayer player, Side side) {
        super.applyPerkLogic(player, side);

        PlayerAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, side);
        for (PerkAttributeModifier modifier : typeModifierList) {
            List<PerkAttributeModifier> modify = new Stack<>();
            modify.add(modifier);
            modify.addAll(attr.gainModifiers(modifier, this));
            for (PerkAttributeModifier mod : modify) {
                mod = attr.convertModifier(modifier, this);
                attr.applyModifier(player, mod.getAttributeType(), mod);
            }
        }
    }

    @Override
    public void removePerkLogic(EntityPlayer player, Side side) {
        super.removePerkLogic(player, side);

        PlayerAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, side);
        for (PerkAttributeModifier modifier : typeModifierList) {
            List<PerkAttributeModifier> modify = new Stack<>();
            modify.add(modifier);
            modify.addAll(attr.gainModifiers(modifier, this));
            for (PerkAttributeModifier mod : modify) {
                mod = attr.convertModifier(modifier, this);
                attr.removeModifier(player, mod.getAttributeType(), mod);
            }
        }
    }
}
