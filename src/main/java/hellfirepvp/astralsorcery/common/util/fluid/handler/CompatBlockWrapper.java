/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2019
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.fluid.handler;

import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidStack;
import hellfirepvp.astralsorcery.common.util.fluid.CompatFluidUtil;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: CompatBlockWrapper
 * Created by HellFirePvP
 * Date: 19.07.2019 / 15:47
 */
public class CompatBlockWrapper extends CompatVoidFluidHandler {

    protected final Block block;
    protected final World world;
    protected final BlockPos blockPos;

    public CompatBlockWrapper(Block block, World world, BlockPos blockPos) {
        this.block = block;
        this.world = world;
        this.blockPos = blockPos;
    }

    @Override
    public int fill(CompatFluidStack resource, boolean doFill) {
        if (resource.getAmount() < CompatFluidStack.BUCKET_VOLUME) {
            return 0;
        }
        if (doFill) {
            CompatFluidUtil.destroyBlockOnFluidPlacement(world, blockPos);
            world.setBlockState(blockPos, block.getDefaultState(), Constants.BlockFlags.DEFAULT_AND_RERENDER);
        }
        return CompatFluidStack.BUCKET_VOLUME;
    }

}
