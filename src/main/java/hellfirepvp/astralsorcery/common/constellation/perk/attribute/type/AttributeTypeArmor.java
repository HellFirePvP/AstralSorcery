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
 * Class: AttributeTypeArmor
 * Created by HellFirePvP
 * Date: 08.07.2018 / 23:34
 */
public class AttributeTypeArmor extends PerkAttributeType {

    private static final UUID ARMOR_ADD_ID = UUID.fromString("92AAF3D7-D1CD-44CD-A721-7975FBFDB763");
    private static final UUID ARMOR_ADD_MULTIPLY_ID = UUID.fromString("92AAF3D7-C4CD-44CD-A721-7975FBFDB763");
    private static final UUID ARMOR_STACK_MULTIPLY_ID = UUID.fromString("92AAF3D7-FF4D-44CD-A721-7975FBFDB763");

    public AttributeTypeArmor() {
        super(AttributeTypeRegistry.ATTR_TYPE_ARMOR);
    }

    @Override
    public void onModeApply(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side) {
        super.onModeApply(player, mode, side);

        IAttributeInstance attr = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
        switch (mode) {
            case ADDITION:
                attr.applyModifier(new DynamicPlayerAttributeModifier(ARMOR_ADD_ID, "Perk Armor Add", getTypeString(), mode, player, side));
                break;
            case ADDED_MULTIPLY:
                attr.applyModifier(new DynamicPlayerAttributeModifier(ARMOR_ADD_MULTIPLY_ID, "Perk Armor Multiply Add", getTypeString(), mode, player, side));
                break;
            case STACKING_MULTIPLY:
                attr.applyModifier(new DynamicPlayerAttributeModifier(ARMOR_STACK_MULTIPLY_ID, "Perk Armor Stack Add", getTypeString(), mode, player, side));
                break;
        }
    }

    @Override
    public void onModeRemove(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side) {
        super.onModeRemove(player, mode, side);

        IAttributeInstance attr = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
        switch (mode) {
            case ADDITION:
                attr.removeModifier(ARMOR_ADD_ID);
                break;
            case ADDED_MULTIPLY:
                attr.removeModifier(ARMOR_ADD_MULTIPLY_ID);
                break;
            case STACKING_MULTIPLY:
                attr.removeModifier(ARMOR_STACK_MULTIPLY_ID);
                break;
        }
    }
}
