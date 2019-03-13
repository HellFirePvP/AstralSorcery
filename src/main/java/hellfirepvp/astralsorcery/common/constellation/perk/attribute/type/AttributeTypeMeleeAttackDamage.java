/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeMeleeAttackDamage
 * Created by HellFirePvP
 * Date: 08.07.2018 / 15:31
 */
public class AttributeTypeMeleeAttackDamage extends VanillaAttributeType {

    private static final UUID MELEE_ATTACK_DAMAGE_BOOST_ADD_ID = UUID.fromString("020E0DFB-87AE-4653-9556-831010FF91A0");
    private static final UUID MELEE_ATTACK_DAMAGE_BOOST_ADD_MULTIPLY_ID = UUID.fromString("020E0DFB-87AE-4653-95D6-831010FF91A1");
    private static final UUID MELEE_ATTACK_DAMAGE_BOOST_STACK_MULTIPLY_ID = UUID.fromString("020E0DFB-87AE-4653-9F56-831010FF91A2");

    public AttributeTypeMeleeAttackDamage() {
        super(AttributeTypeRegistry.ATTR_TYPE_MELEE_DAMAGE);
    }

    @Override
    public IAttribute getAttribute() {
        return SharedMonsterAttributes.ATTACK_DAMAGE;
    }

    @Override
    public String getDescription() {
        return "Perk AttackDamage";
    }

    @Override
    public UUID getID(PerkAttributeModifier.Mode mode) {
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
