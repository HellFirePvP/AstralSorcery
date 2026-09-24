/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2026<p>
 * <p>
 * All rights reserved.<p>
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery<p>
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.starlight;

import hellfirepvp.observerlib.common.event.BlockChangeNotifier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on GitHub.
 * Class: StarlightNetworkLinkHelper
 * Created by HellFirePvP
 * Date: 07.09.2026 / 10:00
 */
public class StarlightNetworkLinkHelper implements BlockChangeNotifier.Listener {

    private static final StarlightNetworkLinkHelper INSTANCE = new StarlightNetworkLinkHelper();

    private StarlightNetworkLinkHelper() {}

    public static StarlightNetworkLinkHelper getInstance() {
        return INSTANCE;
    }

    @Override
    public void onChange(Level world, LevelChunk chunk, BlockPos pos, BlockState oldState, BlockState newState) {
        if (oldState == newState) return;
        if (world.isClientSide() || !chunk.getPersistedStatus().isOrAfter(ChunkStatus.FULL)) {
            return;
        }

        StarlightNetworkLevelHelper handle = StarlightNetworkLevelHelper.get(world);
        handle.notifyBlockChange(pos);
    }
}
