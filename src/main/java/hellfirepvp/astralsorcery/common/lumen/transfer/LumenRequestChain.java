/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.lumen.transfer;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtil;
import hellfirepvp.astralsorcery.common.visual.type.LumenTransferEffect;
import hellfirepvp.astralsorcery.common.lumen.Lumen;
import hellfirepvp.astralsorcery.common.tile.TileLumenFilament;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

import java.util.Deque;
import java.util.LinkedList;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: LumenRequestChain
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class LumenRequestChain {

    private final BlockPos start;
    private final LumenNode endNode;
    private final LinkedList<LumenNode> nodeChain = new LinkedList<>();

    public LumenRequestChain(BlockPos start, LumenNode endNode) {
        this.start = start;
        this.endNode = endNode;
    }

    public final BlockPos getStart() {
        return this.start;
    }

    public LumenNode getEndNode() {
        return this.endNode;
    }

    void appendNode(LumenNode node) {
        this.nodeChain.addLast(node);
    }

    public Deque<LumenNode> getNodeChain() {
        return this.nodeChain;
    }

    public boolean isValid() {
        return !this.nodeChain.isEmpty();
    }

    public boolean reValidateChain(Level level) {
        for (int i = 0; i < this.nodeChain.size() - 1; i++) {
            LumenNode current = this.nodeChain.get(i);
            LumenNode next = this.nodeChain.get(i + 1);

            if (!current.canConnect(level, next)) {
                return false;
            }
        }
        return true;
    }

    public void playTransferEffect(ServerLevel sLevel, Lumen type) {
        LumenTransferEffect.withChain(type, this).sendToNearby(sLevel, BlockPos.ZERO);

        MiscUtil.getTileAt(sLevel, this.getStart(), LumenTransferNotifiable.class, false).ifPresent(tile -> {
            tile.onTransfer(sLevel, type);
        });
        this.getNodeChain().forEach(node -> {
            node.getTile(sLevel, LumenTransferNotifiable.class, false).ifPresent(tile -> {
                tile.onTransfer(sLevel, type);
            });
        });
    }
}
