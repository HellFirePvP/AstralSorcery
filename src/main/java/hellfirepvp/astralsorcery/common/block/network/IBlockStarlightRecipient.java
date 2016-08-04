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

    public void receiveStarlight(World world, BlockPos pos, IBlockState state, Constellation starlightType, double amount);

}
