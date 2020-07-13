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
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypeMaxReach
 * Created by HellFirePvP
 * Date: 24.08.2019 / 23:58
 */
public class AttributeTypeMaxReach extends VanillaAttributeType {

    private static final UUID REACH_ADD_ID = UUID.fromString("E5416922-E446-4E1B-AEE5-04A6B83E17AA");
    private static final UUID REACH_ADD_MULTIPLY_ID = UUID.fromString("E5DD6922-A49F-4E1B-AEE5-04A6B83E17AA");
    private static final UUID REACH_STACK_MULTIPLY_ID = UUID.fromString("E5DD6922-11D4-4E1B-AEE5-04A6B83E17AA");

    public AttributeTypeMaxReach() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_REACH);
    }

    @Override
    public String getDescription() {
        return "Perk MaxReach";
    }

    @Nonnull
    @Override
    public IAttribute getAttribute() {
        return PlayerEntity.REACH_DISTANCE;
    }

    @Override
    public UUID getID(ModifierType mode) {
        switch (mode) {
            case ADDITION:
                return REACH_ADD_ID;
            case ADDED_MULTIPLY:
                return REACH_ADD_MULTIPLY_ID;
            case STACKING_MULTIPLY:
                return REACH_STACK_MULTIPLY_ID;
            default:
                break;
        }
        return null;
    }
}
