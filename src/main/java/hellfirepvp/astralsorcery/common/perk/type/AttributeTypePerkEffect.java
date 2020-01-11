/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.lib.PerkAttributeTypesAS;
import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.type.vanilla.VanillaAttributeType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: AttributeTypePerkEffect
 * Created by HellFirePvP
 * Date: 25.08.2019 / 17:12
 */
public class AttributeTypePerkEffect extends PerkAttributeType {

    public AttributeTypePerkEffect() {
        super(PerkAttributeTypesAS.KEY_ATTR_TYPE_INC_PERK_EFFECT, true);
    }

    @Override
    public void onApply(PlayerEntity player, LogicalSide side) {
        super.onApply(player, side);

        RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.getValues()
                .stream()
                .filter(t -> t instanceof VanillaAttributeType)
                .forEach(t -> ((VanillaAttributeType) t).refreshAttribute(player));
    }

    @Override
    public void onRemove(PlayerEntity player, LogicalSide side, boolean removedCompletely) {
        super.onRemove(player, side, removedCompletely);

        RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.getValues()
                .stream()
                .filter(t -> t instanceof VanillaAttributeType)
                .forEach(t -> ((VanillaAttributeType) t).refreshAttribute(player));
    }

}
