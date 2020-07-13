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
 * Class: AttributeTypeMovementSpeed
 * Created by HellFirePvP
 * Date: 25.08.2019 / 00:00
 */
public class AttributeTypeMovementSpeed extends VanillaAttributeType {

    private static final UUID MOVE_SPEED_ADD_ID = UUID.fromString("0E769034-8C58-48A1-88ED-1908F602E127");
    private static final UUID MOVE_SPEED_ADD_MULTIPLY_ID = UUID.fromString("0E769034-8CDD-48A1-88ED-1908F602E127");
    private static final UUID MOVE_SPEED_STACK_MULTIPLY_ID = UUID.fromString("0E769034-8C14-48A1-88ED-1908F602E127");

    public AttributeTypeMovementSpeed() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_MOVESPEED);
    }

    @Nonnull
    @Override
    public IAttribute getAttribute() {
        return SharedMonsterAttributes.MOVEMENT_SPEED;
    }

    @Override
    public String getDescription() {
        return "Perk MoveSpeed";
    }

    @Override
    public UUID getID(ModifierType mode) {
        switch (mode) {
            case ADDITION:
                return MOVE_SPEED_ADD_ID;
            case ADDED_MULTIPLY:
                return MOVE_SPEED_ADD_MULTIPLY_ID;
            case STACKING_MULTIPLY:
                return MOVE_SPEED_STACK_MULTIPLY_ID;
            default:
                break;
        }
        return null;
    }

}
