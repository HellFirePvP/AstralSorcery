/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeArmorToughness
 * Created by HellFirePvP
 * Date: 11.08.2018 / 11:33
 */
public class AttributeTypeArmorToughness extends VanillaAttributeType {

    private static final UUID ARMOR_TOUGHNESS_ADD_ID = UUID.fromString("36DD43BF-0ACB-94AB-809B-D07F0FB060D5");
    private static final UUID ARMOR_TOUGHNESS_ADD_MULTIPLY_ID = UUID.fromString("36DD43BF-0ACB-40E4-809B-D07F0FB060D5");
    private static final UUID ARMOR_TOUGHNESS_STACK_MULTIPLY_ID = UUID.fromString("36DD43BF-0ACB-FF51-809B-D07F0FB060D5");

    public AttributeTypeArmorToughness() {
        super(AttributeTypeRegistry.ATTR_TYPE_ARMOR_TOUGHNESS);
    }

    @Override
    public String getDescription() {
        return "Perk Armor Toughness";
    }

    @Override
    public IAttribute getAttribute() {
        return SharedMonsterAttributes.ARMOR_TOUGHNESS;
    }

    @Override
    public UUID getID(PerkAttributeModifier.Mode mode) {
        switch (mode) {
            case ADDITION:
                return ARMOR_TOUGHNESS_ADD_ID;
            case ADDED_MULTIPLY:
                return ARMOR_TOUGHNESS_ADD_MULTIPLY_ID;
            case STACKING_MULTIPLY:
                return ARMOR_TOUGHNESS_STACK_MULTIPLY_ID;
        }
        return null;
    }
}
