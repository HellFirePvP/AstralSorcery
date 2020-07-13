/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.util.block;

import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: TestBlockUseContext
 * Created by HellFirePvP
 * Date: 29.02.2020 / 12:07
 */
public class TestBlockUseContext extends BlockItemUseContext {

    private final Entity entity;

    private TestBlockUseContext(World worldIn, @Nullable Entity usingEntity, Hand hand, ItemStack stack, BlockPos at, Direction side) {
        super(worldIn, null, hand, stack, new BlockRayTraceResult(new Vec3d(at).add(0.5, 0.5, 0.5), side, at, false));
        this.entity = usingEntity;
    }

    public static BlockItemUseContext getHandContext(World worldIn, @Nullable Entity usingEntity, Hand usedHand, BlockPos at, Direction side) {
        return getHandContextWithItem(worldIn, usingEntity, usedHand, ItemStack.EMPTY, at, side);
    }

    public static BlockItemUseContext getHandContextWithItem(World worldIn, @Nullable Entity usingEntity, Hand usedHand, ItemStack stack, BlockPos at, Direction side) {
        return new TestBlockUseContext(worldIn, usingEntity, usedHand, stack, at, side);
    }

    @Override
    public Direction getPlacementHorizontalFacing() {
        return this.entity == null ? Direction.NORTH : Direction.fromAngle(this.entity.rotationYaw);
    }

    @Override
    public Direction getNearestLookingDirection() {
        return Direction.getFacingDirections(this.entity)[0];
    }

    @Override
    public Direction[] getNearestLookingDirections() {
        Direction[] adirection = Direction.getFacingDirections(this.entity);
        if (this.replaceClicked) {
            return adirection;
        } else {
            Direction direction = this.getFace();

            int i;
            i = 0;
            while (i < adirection.length && adirection[i] != direction.getOpposite()) {
                ++i;
            }

            if (i > 0) {
                System.arraycopy(adirection, 0, adirection, 1, i);
                adirection[0] = direction.getOpposite();
            }
            return adirection;
        }
    }

    @Override
    public boolean func_225518_g_() {
        return false;
    }

    @Override
    public float getPlacementYaw() {
        return 0F;
    }
}
