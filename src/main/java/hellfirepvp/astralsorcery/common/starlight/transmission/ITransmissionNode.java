/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission;

import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;

import java.util.LinkedList;
import java.util.List;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: ITransmissionNode
 * Created by HellFirePvP
 * Date: 03.08.2016 / 10:43
 */
public interface ITransmissionNode extends IPrismTransmissionNode {

    public NodeConnection<IPrismTransmissionNode> queryNextNode(WorldNetworkHandler handler);

    default public List<NodeConnection<IPrismTransmissionNode>> queryNext(WorldNetworkHandler handler) {
        List<NodeConnection<IPrismTransmissionNode>> nodes = new LinkedList<>();
        NodeConnection<IPrismTransmissionNode> next = queryNextNode(handler);
        if (next != null) {
            nodes.add(next);
        }
        return nodes;
    }

}
