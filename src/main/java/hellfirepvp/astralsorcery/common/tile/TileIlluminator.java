/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.tile;

import hellfirepvp.astralsorcery.common.tile.base.TileEntityTick;
import hellfirepvp.astralsorcery.common.util.MiscUtils;
import hellfirepvp.astralsorcery.common.util.block.BlockPredicate;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TileIlluminator
 * Created by HellFirePvP
 * Date: 31.08.2019 / 22:44
 */
public class TileIlluminator extends TileEntityTick {

    //TODO actually implement this
    public static final LightCheck ILLUMINATOR_CHECK = new LightCheck();

    protected TileIlluminator(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public static class LightCheck implements BlockPredicate {

        @Override
        public boolean test(World world, BlockPos pos, BlockState state) {
            return world.isAirBlock(pos) &&
                    !MiscUtils.canSeeSky(world, pos, false, false) &&
                    world.getLight(pos) < 8 &&
                    world.getLightFor(LightType.SKY, pos) < 6;
        }

    }
}
