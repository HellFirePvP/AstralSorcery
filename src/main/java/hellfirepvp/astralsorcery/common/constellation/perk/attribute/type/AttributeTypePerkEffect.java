/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.constellation.perk.attribute.type;

import hellfirepvp.astralsorcery.common.constellation.perk.attribute.AttributeTypeRegistry;
import hellfirepvp.astralsorcery.common.constellation.perk.attribute.PerkAttributeType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypePerkEffect
 * Created by HellFirePvP
 * Date: 10.07.2018 / 18:04
 */
public class AttributeTypePerkEffect extends PerkAttributeType {

    public AttributeTypePerkEffect() {
        super(AttributeTypeRegistry.ATTR_TYPE_INC_PERK_EFFECT, true);
    }

    @Override
    public void onApply(EntityPlayer player, Side side) {
        super.onApply(player, side);

        AttributeTypeRegistry.getTypes()
                .stream()
                .filter(t -> t instanceof VanillaAttributeType)
                .forEach(t -> ((VanillaAttributeType) t).refreshAttribute(player));
    }

    @Override
    public void onRemove(EntityPlayer player, Side side, boolean removedCompletely) {
        super.onRemove(player, side, removedCompletely);

        AttributeTypeRegistry.getTypes()
                .stream()
                .filter(t -> t instanceof VanillaAttributeType)
                .forEach(t -> ((VanillaAttributeType) t).refreshAttribute(player));
    }
}
