/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.api;

import hellfirepvp.astralsorcery.common.starlight.transmission.StarlightTransmissionPacket;
import net.minecraft.server.level.ServerLevel;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TransmissionReceiverNode
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface TransmissionReceiverNode extends TransmissionNode {

    void receiveStarlight(ServerLevel sLevel, StarlightTransmissionPacket packet);

}
