/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight.transmission;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

/**
* This class is part of the Astral Sorcery Mod
* The complete source code for this mod can be found on github.
* Class: NodeConnection
* Created by HellFirePvP
* Date: 03.08.2016 / 23:11
*/
public class NodeConnection<T extends IPrismTransmissionNode> {

    private final T node;
    private final BlockPos to;
    private final boolean canConnect;

    public NodeConnection(@Nullable T node, BlockPos to, boolean canConnect) {
        this.node = node;
        this.to = to;
        this.canConnect = canConnect;
    }

    public BlockPos getTo() {
        return to;
    }

    @Nullable
    public T getNode() {
        return node;
    }

    public boolean canConnect() {
        return canConnect;
    }
}
