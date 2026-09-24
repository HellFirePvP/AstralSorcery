/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.tooltip;

import hellfirepvp.astralsorcery.common.component.IdentifierComponent;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ArtifactDecoratedTooltip
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record ArtifactDecoratedTooltip(IdentifierComponent identifier, FormattedText decorated) implements TooltipComponent {
}
