/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.linking;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: BlockLinkConnection
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public final class BlockLinkConnection {

    public static final Codec<BlockLinkConnection> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            BlockPos.CODEC.fieldOf("to").forGetter(BlockLinkConnection::getTo),
            Codec.DOUBLE.fieldOf("distanceSq").forGetter(BlockLinkConnection::getDistanceSq),
            Codec.BOOL.fieldOf("canConnect").forGetter(BlockLinkConnection::canConnect)
    ).apply(inst, BlockLinkConnection::new));

    private final BlockPos to;
    private final double distanceSq;
    private boolean canConnect;

    public BlockLinkConnection(BlockPos to, double distanceSq, boolean canConnect) {
        this.to = to;
        this.distanceSq = distanceSq;
        this.canConnect = canConnect;
    }

    public BlockPos getTo() {
        return this.to;
    }

    public double getDistanceSq() {
        return this.distanceSq;
    }

    public boolean canConnect() {
        return this.canConnect;
    }

    public void setCanConnect(boolean canConnect) {
        this.canConnect = canConnect;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BlockLinkConnection that = (BlockLinkConnection) o;
        return Objects.equals(to, that.to);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(to);
    }
}
