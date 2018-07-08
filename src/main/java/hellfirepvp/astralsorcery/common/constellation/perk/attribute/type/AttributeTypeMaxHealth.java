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
public class AttributeTypeMaxHealth extends PerkAttributeType {

    private static final UUID MAX_HEALTH_ADD_ID = UUID.fromString("1FA85BB6-C2CF-45A3-A880-68045A46Dc39");
    private static final UUID MAX_HEALTH_ADD_MULTIPLY_ID = UUID.fromString("1FA85BB6-4ECF-45A3-A880-68045A46Dc39");
    private static final UUID MAX_HEALTH_STACK_MULTIPLY_ID = UUID.fromString("1FA85BB6-F6CF-45A3-A880-68045A46Dc39");

    public AttributeTypeMaxHealth() {
        super(AttributeTypeRegistry.ATTR_TYPE_HEALTH);
    }

    @Override
    public void onModeApply(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side) {
        super.onModeApply(player, mode, side);

        IAttributeInstance attr = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
        switch (mode) {
            case ADDITION:
                attr.applyModifier(new DynamicPlayerAttributeModifier(MAX_HEALTH_ADD_ID, "Perk MaxHealth Add", getTypeString(), mode, player, side));
                break;
            case ADDED_MULTIPLY:
                attr.applyModifier(new DynamicPlayerAttributeModifier(MAX_HEALTH_ADD_MULTIPLY_ID, "Perk MaxHealth Multiply Add", getTypeString(), mode, player, side));
                break;
            case STACKING_MULTIPLY:
                attr.applyModifier(new DynamicPlayerAttributeModifier(MAX_HEALTH_STACK_MULTIPLY_ID, "Perk MaxHealth Stack Add", getTypeString(), mode, player, side));
                break;
        }
    }

    @Override
    public void onModeRemove(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side) {
        super.onModeRemove(player, mode, side);

        IAttributeInstance attr = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
        switch (mode) {
            case ADDITION:
                attr.removeModifier(MAX_HEALTH_ADD_ID);
                break;
            case ADDED_MULTIPLY:
                attr.removeModifier(MAX_HEALTH_ADD_MULTIPLY_ID);
                break;
            case STACKING_MULTIPLY:
                attr.removeModifier(MAX_HEALTH_STACK_MULTIPLY_ID);
                break;
        }
    }
}
