/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.data;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;

import java.util.Objects;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: ChunkSectionPos
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class ChunkSectionPos {

    private final ChunkPos chunkPos;
    private final int yLayer;

    public ChunkSectionPos(ChunkPos chunkPos, int yLayer) {
        this.chunkPos = chunkPos;
        this.yLayer = yLayer;
    }

    public static ChunkSectionPos of(BlockPos pos) {
        return new ChunkSectionPos(new ChunkPos(pos), Mth.floor(pos.getY() / 16F));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChunkSectionPos that = (ChunkSectionPos) o;
        return this.yLayer == that.yLayer && Objects.equals(this.chunkPos, that.chunkPos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.chunkPos, this.yLayer);
    }

    @Override
    public String toString() {
        return "ChunkSectionPos{" +
                "chunkPos=" + this.chunkPos +
                ", yLayer=" + this.yLayer +
                '}';
    }
}
