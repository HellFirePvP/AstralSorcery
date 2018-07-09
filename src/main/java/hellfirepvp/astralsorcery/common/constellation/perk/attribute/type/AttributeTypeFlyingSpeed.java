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
 * Class: AttributeTypeFlyingSpeed
 * Created by HellFirePvP
 * Date: 09.07.2018 / 16:31
 */
public class AttributeTypeFlyingSpeed extends VanillaAttributeType {

    private static final UUID FLY_SPEED_ADD_ID = UUID.fromString("459A7BC0-51B4-444A-A478-AB346736DF8A");
    private static final UUID FLY_SPEED_ADD_MULTIPLY_ID = UUID.fromString("459A7BC0-51B4-564A-A478-AB346736DF8A");
    private static final UUID FLY_SPEED_STACK_MULTIPLY_ID = UUID.fromString("459A7BC0-51B4-F14A-A478-AB346736DF8A");

    public AttributeTypeFlyingSpeed() {
        super(AttributeTypeRegistry.ATTR_TYPE_FLYSPEED);
    }

    @Override
    public IAttribute getAttribute() {
        return SharedMonsterAttributes.FLYING_SPEED;
    }

    @Override
    public String getDescription() {
        return "Perk FlySpeed";
    }

    @Override
    public UUID getID(PerkAttributeModifier.Mode mode) {
        switch (mode) {
            case ADDITION:
                return FLY_SPEED_ADD_ID;
            case ADDED_MULTIPLY:
                return FLY_SPEED_ADD_MULTIPLY_ID;
            case STACKING_MULTIPLY:
                return FLY_SPEED_STACK_MULTIPLY_ID;
        }
        return null;
    }

}
