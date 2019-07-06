/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission.registry;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TransmissionProvider
 * Created by HellFirePvP
 * Date: 30.06.2019 / 22:02
 */
public abstract class TransmissionProvider implements Supplier<IPrismTransmissionNode> {

    private String identifierCache = null;

    @Nonnull
    public String getIdentifier() {
        if (identifierCache == null) {
            identifierCache = AstralSorcery.MODID + ":" + get().getClass().getSimpleName();
        }
        return identifierCache;
    }

}
