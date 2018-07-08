/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.tree.nodes;

import hellfirepvp.astralsorcery.common.constellation.perk.AbstractPerk;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeHelper;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.type.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.tree.PerkTreePoint;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: PerkGenericIncreaseAttackDamage
 * Created by HellFirePvP
 * Date: 08.07.2018 / 15:45
 */
public class PerkGenericIncreaseAttackDamage extends AbstractPerk {

    private PerkAttributeModifier modifier;

    public PerkGenericIncreaseAttackDamage(String name, int x, int y, float dmgIncrease) {
        super(name, x, y);
        this.modifier = new PerkAttributeModifier(PerkAttributeModifier.Mode.ADDED_MULTIPLY, dmgIncrease);
    }

    @Override
    public PerkTreePoint getPoint() {
        return new PerkTreePoint(this, getOffset());
    }

    @Override
    public void applyPerk(EntityPlayer player, Side side) {
        PerkAttributeHelper.getOrCreateMap(player, side).applyModifier(player, AttributeTypeRegistry.ATTR_TYPE_DAMAGE, this.modifier);
    }

    @Override
    public void removePerk(EntityPlayer player, Side side) {
        PerkAttributeHelper.getOrCreateMap(player, side).removeModifier(player, AttributeTypeRegistry.ATTR_TYPE_DAMAGE, this.modifier);
    }

}
