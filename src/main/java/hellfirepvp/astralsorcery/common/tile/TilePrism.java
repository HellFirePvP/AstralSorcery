/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.block.tile.BlockPrism;
import hellfirepvp.astralsorcery.common.lib.TileEntityTypesAS;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.tile.network.StarlightTransmissionPrism;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TilePrism
 * Created by HellFirePvP
 * Date: 24.08.2019 / 23:13
 */
public class TilePrism extends TileLens {

    public TilePrism() {
        super(TileEntityTypesAS.PRISM);
    }

    @Override
    public boolean isSingleLink() {
        return false;
    }

    @Override
    public Direction getPlacedAgainst() {
        BlockState state = world.getBlockState(getPos());
        if (!(state.getBlock() instanceof BlockPrism)) {
            return Direction.DOWN;
        }
        return state.get(BlockPrism.PLACED_AGAINST);
    }

    @Nonnull
    @Override
    public IPrismTransmissionNode provideTransmissionNode(BlockPos at) {
        return new StarlightTransmissionPrism(at, this.getAttributes());
    }
}
