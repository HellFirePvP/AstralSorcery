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
 * Class: AttributeTypeMovementSpeed
 * Created by HellFirePvP
 * Date: 08.07.2018 / 21:03
 */
public class AttributeTypeMovementSpeed extends PerkAttributeType {

    private static final UUID MOVE_SPEED_ADD_ID = UUID.fromString("0E769034-8C58-48A1-88ED-1908F602E127");
    private static final UUID MOVE_SPEED_ADD_MULTIPLY_ID = UUID.fromString("0E769034-8CDD-48A1-88ED-1908F602E127");
    private static final UUID MOVE_SPEED_STACK_MULTIPLY_ID = UUID.fromString("0E769034-8C14-48A1-88ED-1908F602E127");

    public AttributeTypeMovementSpeed() {
        super(AttributeTypeRegistry.ATTR_TYPE_MOVESPEED);
    }

    @Override
    public void onModeApply(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side) {
        super.onModeApply(player, mode, side);

        IAttributeInstance attr = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
        switch (mode) {
            case ADDITION:
                attr.applyModifier(new DynamicPlayerAttributeModifier(MOVE_SPEED_ADD_ID, "Perk MoveSpeed Add", getTypeString(), mode, player, side));
                break;
            case ADDED_MULTIPLY:
                attr.applyModifier(new DynamicPlayerAttributeModifier(MOVE_SPEED_ADD_MULTIPLY_ID, "Perk MoveSpeed Multiply Add", getTypeString(), mode, player, side));
                break;
            case STACKING_MULTIPLY:
                attr.applyModifier(new DynamicPlayerAttributeModifier(MOVE_SPEED_STACK_MULTIPLY_ID, "Perk MoveSpeed Stack Add", getTypeString(), mode, player, side));
                break;
        }
    }

    @Override
    public void onModeRemove(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side) {
        super.onModeRemove(player, mode, side);

        IAttributeInstance attr = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
        switch (mode) {
            case ADDITION:
                attr.removeModifier(MOVE_SPEED_ADD_ID);
                break;
            case ADDED_MULTIPLY:
                attr.removeModifier(MOVE_SPEED_ADD_MULTIPLY_ID);
                break;
            case STACKING_MULTIPLY:
                attr.removeModifier(MOVE_SPEED_STACK_MULTIPLY_ID);
                break;
        }
    }

}
