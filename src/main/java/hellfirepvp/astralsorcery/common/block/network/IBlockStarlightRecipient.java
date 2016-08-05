package hellfirepvp.astralsorcery.common.block.network;

import hellfirepvp.astralsorcery.common.constellation.Constellation;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: IBlockStarlightRecipient
 * Created by HellFirePvP
 * Date: 04.08.2016 / 22:27
 */
public interface IBlockStarlightRecipient {

    /**
     * Called when this block receives starlight from the network broadcast
     * This is only called if the chunk it is in is also loaded!
     *
     * For a Chunk independent implementation check the tile entities and ITransmissionReceiver
     * as well as its implementations
     *
     * Note that this is only fired if this block is a block linked to an endpoint of a network
     * and if this block is not a transmission node.
     *
     * @param world the world this block instance is in
     * @param pos the position
     * @param starlightType the constellation type of the starlight received
     * @param amount the amount received
     */
    public void receiveStarlight(World world, BlockPos pos, Constellation starlightType, double amount);

}
