/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen;

import hellfirepvp.astralsorcery.common.util.data.ColorWrapper;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenPrismatic
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenPrismatic extends LumenDynamicColor {

    public LumenPrismatic() {
        super(tick -> ColorWrapper.ofHSB((int) (tick % 240) / 240F, 0.65F, 1F));
    }
}
