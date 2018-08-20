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
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

import java.util.UUID;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: VanillaAttributeType
 * Created by HellFirePvP
 * Date: 09.07.2018 / 17:58
 */
public abstract class VanillaAttributeType extends PerkAttributeType {

    public VanillaAttributeType(String type) {
        super(type);
    }

    @Override
    public void onApply(EntityPlayer player, Side side) {
        super.onApply(player, side);

        refreshAttribute(player);
    }

    @Override
    public void onRemove(EntityPlayer player, Side side, boolean removedCompletely) {
        super.onRemove(player, side, removedCompletely);

        refreshAttribute(player);
    }

    @Override
    public void onModeApply(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side) {
        super.onModeApply(player, mode, side);

        IAttributeInstance attr = player.getAttributeMap().getAttributeInstance(getAttribute());

        //The attributes don't get written/read from bytebuffer on local connection, but ARE in dedicated connections.
        //Remove minecraft's dummy instances in case we're on a dedicated server.
        if (side == Side.CLIENT) {
            AttributeModifier modifier;
            if ((modifier = attr.getModifier(getID(mode))) != null) {
                if (!(modifier instanceof DynamicPlayerAttributeModifier)) {
                    attr.removeModifier(getID(mode));
                } else {
                    return;
                }
            }
        }

        switch (mode) {
            case ADDITION:
                attr.applyModifier(new DynamicPlayerAttributeModifier(getID(mode), getDescription() + " Add", getTypeString(), mode, player, side));
                break;
            case ADDED_MULTIPLY:
                attr.applyModifier(new DynamicPlayerAttributeModifier(getID(mode), getDescription() + " Multiply Add", getTypeString(), mode, player, side));
                break;
            case STACKING_MULTIPLY:
                attr.applyModifier(new DynamicPlayerAttributeModifier(getID(mode), getDescription() + " Stack Add", getTypeString(), mode, player, side));
                break;
        }
    }

    @Override
    public void onModeRemove(EntityPlayer player, PerkAttributeModifier.Mode mode, Side side, boolean removedCompletely) {
        super.onModeRemove(player, mode, side, removedCompletely);

        IAttributeInstance attr = player.getAttributeMap().getAttributeInstance(getAttribute());
        switch (mode) {
            case ADDITION:
                attr.removeModifier(getID(mode));
                break;
            case ADDED_MULTIPLY:
                attr.removeModifier(getID(mode));
                break;
            case STACKING_MULTIPLY:
                attr.removeModifier(getID(mode));
                break;
        }
    }

    public void refreshAttribute(EntityPlayer player) {
        IAttributeInstance attr = player.getAttributeMap().getAttributeInstance(getAttribute());
        double base = attr.getBaseValue();
        if (base == 0) {
            attr.setBaseValue(1);
        } else {
            attr.setBaseValue(0);
        }
        attr.setBaseValue(base);
    }

    public abstract UUID getID(PerkAttributeModifier.Mode mode);

    public abstract String getDescription();

    public abstract IAttribute getAttribute();

}
