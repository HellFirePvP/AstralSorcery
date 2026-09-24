/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.item.base;

import net.minecraft.core.cauldron.CauldronInteraction;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: CauldronInteractableItem
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface CauldronInteractableItem {

    CauldronInteraction.InteractionMap getInteractionMap();

    CauldronInteraction getInteraction();

}
