/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.type;

import hellfirepvp.astralsorcery.common.lib.RegistriesAS;
import hellfirepvp.astralsorcery.common.perk.source.ModifierSource;
import hellfirepvp.astralsorcery.common.perk.type.base.PerkAttributeType;
import hellfirepvp.astralsorcery.common.perk.type.base.VanillaPerkAttributeType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttributeTypePerkEffect
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class AttributeTypePerkEffect extends PerkAttributeType {

    public AttributeTypePerkEffect() {
        super(true);
    }

    @Override
    public void onApply(Player player, LogicalSide side, ModifierSource source) {
        super.onApply(player, side, source);

        RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.stream()
                .filter(t -> t instanceof VanillaPerkAttributeType)
                .forEach(t -> ((VanillaPerkAttributeType) t).refreshAttribute(player, side));
    }

    @Override
    public void onRemove(Player player, LogicalSide side, boolean removedCompletely, ModifierSource source) {
        super.onRemove(player, side, removedCompletely, source);

        RegistriesAS.REGISTRY_PERK_ATTRIBUTE_TYPES.stream()
                .filter(t -> t instanceof VanillaPerkAttributeType)
                .forEach(t -> ((VanillaPerkAttributeType) t).refreshAttribute(player, side));
    }
}
