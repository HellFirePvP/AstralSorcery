/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeMaxReach
 * Created by HellFirePvP
 * Date: 14.07.2018 / 11:06
 */
public class AttributeTypeMaxReach extends VanillaAttributeType {

    private static final UUID REACH_ADD_ID = UUID.fromString("E5416922-E446-4E1B-AEE5-04A6B83E17AA");
    private static final UUID REACH_ADD_MULTIPLY_ID = UUID.fromString("E5DD6922-A49F-4E1B-AEE5-04A6B83E17AA");
    private static final UUID REACH_STACK_MULTIPLY_ID = UUID.fromString("E5DD6922-11D4-4E1B-AEE5-04A6B83E17AA");

    public AttributeTypeMaxReach() {
        super(AttributeTypeRegistry.ATTR_TYPE_REACH);
    }

    @Override
    public UUID getID(PerkAttributeModifier.Mode mode) {
        switch (mode) {
            case ADDITION:
                return REACH_ADD_ID;
            case ADDED_MULTIPLY:
                return REACH_ADD_MULTIPLY_ID;
            case STACKING_MULTIPLY:
                return REACH_STACK_MULTIPLY_ID;
        }
        return null;
    }

    @Override
    public String getDescription() {
        return "Perk MaxReach";
    }

    @Override
    public IAttribute getAttribute() {
        return EntityPlayer.REACH_DISTANCE;
    }
}
