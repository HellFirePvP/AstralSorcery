/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2018
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.event;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockModifyEvent
 * Created by HellFirePvP
 * Date: 04.08.2016 / 10:49
 */
public class BlockModifyEvent extends Event {

    private final Chunk chunk;
    private final World world;
    private final BlockPos at;
    private final IBlockState oldState, newState;

    public BlockModifyEvent(Chunk chunk, BlockPos at, IBlockState oldState, IBlockState newState) {
        this.at = at;
        this.chunk = chunk;
        this.world = chunk.getWorld();
        this.oldState = oldState;
        this.newState = newState;
    }

    public BlockPos getPos() {
        return at;
    }

    public World getWorld() {
        return world;
    }

    public Chunk getChunk() {
        return chunk;
    }

    @Nullable
    public TileEntity getTileEntity() {
        return world.getTileEntity(getPos());
    }

    public IBlockState getOldState() {
        return oldState;
    }

    public IBlockState getNewState() {
        return newState;
    }

    public Block getOldBlock() {
        return oldState.getBlock();
    }

    public Block getNewBlock() {
        return newState.getBlock();
    }

}
