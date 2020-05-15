/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event.handler;

import hellfirepvp.astralsorcery.common.block.tile.BlockAltar;
import hellfirepvp.astralsorcery.common.starlight.WorldNetworkHandler;
import hellfirepvp.observerlib.common.event.BlockChangeNotifier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: EventHandlerAutoLink
 * Created by HellFirePvP
 * Date: 15.05.2020 / 16:59
 */
public class EventHandlerAutoLink implements BlockChangeNotifier.Listener {

    @Override
    public void onChange(World world, Chunk chunk, BlockPos pos, BlockState oldState, BlockState newState) {
        if (world.isRemote() || !chunk.getStatus().isAtLeast(ChunkStatus.FULL)) {
            return;
        }

        Block oldB = oldState.getBlock();
        Block newB = newState.getBlock();

        if (oldB != newB) {
            WorldNetworkHandler handle = WorldNetworkHandler.getNetworkHandler(world);
            handle.informBlockChange(pos);

            if (oldB == Blocks.CRAFTING_TABLE) {
                handle.removeAutoLinkTo(pos);
            }
            if (newB == Blocks.CRAFTING_TABLE) {
                handle.attemptAutoLinkTo(pos);
            }
            if (oldB instanceof BlockAltar) {
                handle.removeAutoLinkTo(pos);
            }
            if (newB instanceof BlockAltar) {
                handle.attemptAutoLinkTo(pos);
            }
        }
    }
}
