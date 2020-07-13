/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.perk.type.vanilla.VanillaAttributeType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeMeleeAttackDamage
 * Created by HellFirePvP
 * Date: 24.08.2019 / 23:59
 */
public class AttributeTypeMeleeAttackDamage extends VanillaAttributeType {

    private static final UUID MELEE_ATTACK_DAMAGE_BOOST_ADD_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010FF91A0");
    private static final UUID MELEE_ATTACK_DAMAGE_BOOST_ADD_MULTIPLY_ID = UUID.fromString("020E0DFB-87AE-4653-95D6-831010FF91A1");
    private static final UUID MELEE_ATTACK_DAMAGE_BOOST_STACK_MULTIPLY_ID = UUID.fromString("020E0DFB-87AE-4653-9F56-831010FF91A2");

    public AttributeTypeMeleeAttackDamage() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_MELEE_DAMAGE);
    }

    @Nonnull
    @Override
    public IAttribute getAttribute() {
        return SharedMonsterAttributes.ATTACK_DAMAGE;
    }

    @Override
    public String getDescription() {
        return "Perk AttackDamage";
    }

    @Override
    public UUID getID(ModifierType mode) {
        switch (mode) {
            case ADDITION:
                return MELEE_ATTACK_DAMAGE_BOOST_ADD_ID;
            case ADDED_MULTIPLY:
                return MELEE_ATTACK_DAMAGE_BOOST_ADD_MULTIPLY_ID;
            case STACKING_MULTIPLY:
                return MELEE_ATTACK_DAMAGE_BOOST_STACK_MULTIPLY_ID;
            default:
                break;
        }
        return null;
    }

}
