/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.perk.source;

import hellfirepvp.astralsorcery.common.perk.modifier.PerkAttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;

import java.util.Collection;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: AttributeModifierProvider
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface AttributeModifierProvider {

    Collection<PerkAttributeModifier> getModifiers(Player player, LogicalSide side, boolean ignoreRequirements);

}
