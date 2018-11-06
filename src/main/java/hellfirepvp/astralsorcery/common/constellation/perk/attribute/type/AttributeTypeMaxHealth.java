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
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeMaxHealth
 * Created by HellFirePvP
 * Date: 08.07.2018 / 20:40
 */
public class AttributeTypeMaxHealth extends VanillaAttributeType {

    private static final UUID MAX_HEALTH_ADD_ID = UUID.fromString("1FA85BB6-C2CF-45A3-A880-68045A46Dc39");
    private static final UUID MAX_HEALTH_ADD_MULTIPLY_ID = UUID.fromString("1FA85BB6-4ECF-45A3-A880-68045A46Dc39");
    private static final UUID MAX_HEALTH_STACK_MULTIPLY_ID = UUID.fromString("1FA85BB6-F6CF-45A3-A880-68045A46Dc39");

    public AttributeTypeMaxHealth() {
        super(AttributeTypeRegistry.ATTR_TYPE_HEALTH);
    }

    @Override
    public IAttribute getAttribute() {
        return SharedMonsterAttributes.MAX_HEALTH;
    }

    @Override
    public String getDescription() {
        return "Perk MaxHealth";
    }

    @Override
    public UUID getID(PerkAttributeModifier.Mode mode) {
        switch (mode) {
            case ADDITION:
                return MAX_HEALTH_ADD_ID;
            case ADDED_MULTIPLY:
                return MAX_HEALTH_ADD_MULTIPLY_ID;
            case STACKING_MULTIPLY:
                return MAX_HEALTH_STACK_MULTIPLY_ID;
        }
        return null;
    }
}
