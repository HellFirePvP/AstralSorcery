/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.client.lib;

import hellfirepvp.astralsorcery.client.screen.container.ScreenContainerTomePapers;
import hellfirepvp.astralsorcery.client.screen.container.altar.*;
import hellfirepvp.astralsorcery.common.lib.MenuTypesAS;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: MenuScreensAS
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class MenuScreensAS {

    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(MenuTypesAS.TOME_PAPERS.type(), ScreenContainerTomePapers::new);

        event.register(MenuTypesAS.ALTAR_ILLUMINATION.type(), ScreenContainerAltarIllumination::new);
        event.register(MenuTypesAS.ALTAR_RESONANCE.type(), ScreenContainerAltarResonance::new);
        event.register(MenuTypesAS.ALTAR_LUMINANCE.type(), ScreenContainerAltarLuminance::new);
        event.register(MenuTypesAS.ALTAR_RADIANCE.type(), ScreenContainerAltarRadiance::new);
    }
}
