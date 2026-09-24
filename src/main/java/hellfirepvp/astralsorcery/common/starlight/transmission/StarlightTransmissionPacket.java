/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission;

import hellfirepvp.astralsorcery.common.constellation.BaseConstellation;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StarlightTransmissionPacket
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public record StarlightTransmissionPacket(BaseConstellation constellation, float amount) {

    public StarlightTransmissionPacket withMultiplier(float multiplier) {
        return new StarlightTransmissionPacket(this.constellation, this.amount * multiplier);
    }
}
