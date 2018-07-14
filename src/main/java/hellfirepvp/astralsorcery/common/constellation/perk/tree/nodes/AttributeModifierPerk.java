/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes;

import com.google.common.collect.Lists;
import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PlayerAttributeMap;
import hellfirepvp.astralsorcery.common.util.data.Tuple;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeModifierPerk
 * Created by HellFirePvP
 * Date: 09.07.2018 / 15:10
 */
public class AttributeModifierPerk extends AbstractPerk {

    private List<Tuple<String, PerkAttributeModifier>> typeModifierList = Lists.newArrayList();

    public AttributeModifierPerk(String name, int x, int y) {
        super(name, x, y);
    }

    public <T> T addModifier(float modifier, PerkAttributeModifier.Mode mode, String type) {
        typeModifierList.add(new Tuple<>(type, new PerkAttributeModifier(mode, modifier)));
        return (T) this;
    }

    @Override
    public void applyPerkLogic(EntityPlayer player, Side side) {
        PlayerAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, side);
        this.typeModifierList.forEach(tplMod -> attr.applyModifier(player, tplMod.key, tplMod.value));
    }

    @Override
    public void removePerkLogic(EntityPlayer player, Side side) {
        PlayerAttributeMap attr = PerkAttributeHelper.getOrCreateMap(player, side);
        this.typeModifierList.forEach(tplMod -> attr.removeModifier(player, tplMod.key, tplMod.value));
    }
}
