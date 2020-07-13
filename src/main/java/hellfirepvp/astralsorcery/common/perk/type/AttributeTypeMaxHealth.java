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
 * Class: AttributeTypeMaxHealth
 * Created by HellFirePvP
 * Date: 24.08.2019 / 23:58
 */
public class AttributeTypeMaxHealth extends VanillaAttributeType {

    private static final UUID MAX_HEALTH_ADD_ID = UUID.fromString("1FA85BB6-C2CF-45A3-A880-68045A46Dc39");
    private static final UUID MAX_HEALTH_ADD_MULTIPLY_ID = UUID.fromString("1FA85BB6-4ECF-45A3-A880-68045A46Dc39");
    private static final UUID MAX_HEALTH_STACK_MULTIPLY_ID = UUID.fromString("1FA85BB6-F6CF-45A3-A880-68045A46Dc39");

    public AttributeTypeMaxHealth() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_HEALTH);
    }

    @Nonnull
    @Override
    public IAttribute getAttribute() {
        return SharedMonsterAttributes.MAX_HEALTH;
    }

    @Override
    public String getDescription() {
        return "Perk MaxHealth";
    }

    @Override
    public UUID getID(ModifierType mode) {
        switch (mode) {
            case ADDITION:
                return MAX_HEALTH_ADD_ID;
            case ADDED_MULTIPLY:
                return MAX_HEALTH_ADD_MULTIPLY_ID;
            case STACKING_MULTIPLY:
                return MAX_HEALTH_STACK_MULTIPLY_ID;
            default:
                break;
        }
        return null;
    }
}
