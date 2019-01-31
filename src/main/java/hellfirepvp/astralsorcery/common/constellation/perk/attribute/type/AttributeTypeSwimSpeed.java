/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeSwimSpeed
 * Created by HellFirePvP
 * Date: 12.08.2018 / 08:51
 */
public class AttributeTypeSwimSpeed extends VanillaAttributeType {

    private static final UUID SWIM_SPEED_ADD_ID = UUID.fromString("0E769034-8C58-48A1-88ED-220FA604E147");
    private static final UUID SWIM_SPEED_ADD_MULTIPLY_ID = UUID.fromString("0E769034-8CDD-48A1-88ED-220FA604E147");
    private static final UUID SWIM_SPEED_STACK_MULTIPLY_ID = UUID.fromString("0E769034-8C14-48A1-88ED-220FA604E147");

    public AttributeTypeSwimSpeed() {
        super(AttributeTypeRegistry.ATTR_TYPE_SWIMSPEED);
    }

    @Override
    public UUID getID(PerkAttributeModifier.Mode mode) {
        switch (mode) {
            case ADDITION:
                return SWIM_SPEED_ADD_ID;
            case ADDED_MULTIPLY:
                return SWIM_SPEED_ADD_MULTIPLY_ID;
            case STACKING_MULTIPLY:
                return SWIM_SPEED_STACK_MULTIPLY_ID;
            default:
                break;
        }
        return null;
    }

    @Override
    public String getDescription() {
        return "Perk SwimSpeed";
    }

    @Override
    public IAttribute getAttribute() {
        return EntityLivingBase.SWIM_SPEED;
    }
}
