/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.api;

import com.mojang.serialization.Codec;
import hellfirepvp.astralsorcery.common.starlight.transmission.StarlightTransmissionPacket;
import hellfirepvp.astralsorcery.common.starlight.api.provider.TransmissionSourceNodeProvider;
import net.minecraft.world.level.Level;

import java.util.Optional;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: TransmissionSourceNode
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public interface TransmissionSourceNode extends TransmissionNode {

    Codec<TransmissionSourceNode> CODEC = TransmissionSourceNodeProvider.SOURCE_NODE_CODEC
            .dispatch(TransmissionSourceNode::getProvider, TransmissionSourceNodeProvider::nodeCodec);

    Optional<StarlightTransmissionPacket> produceStarlight(Level level);

    //Get the provider creating instances of this source node
    TransmissionSourceNodeProvider<?> getProvider();

}
