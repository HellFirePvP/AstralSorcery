/*******************************************************************************
 * HellFirePvP / Astral Sorcery 2020
 *
 * All rights reserved.
 * The source code is available on github: https://github.com/HellFirePvP/AstralSorcery
 * For further details, see the License file there.
 ******************************************************************************/

package hellfirepvp.astralsorcery.common.block.foliage;

import hellfirepvp.astralsorcery.common.block.base.template.BlockFlowerTemplate;
import hellfirepvp.astralsorcery.common.block.properties.PropertiesMisc;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;

import javax.annotation.Nonnull;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: BlockGlowFlower
 * Created by HellFirePvP
 * Date: 21.07.2019 / 09:23
 */
public class BlockGlowFlower extends BlockFlowerTemplate implements IPlantable {

    private final VoxelShape shape;

    public BlockGlowFlower() {
        super(PropertiesMisc.defaultTickingPlant()
                .lightValue(5));
        this.shape = createShape();
    }

    private VoxelShape createShape() {
        return Block.makeCuboidShape(1.5, 0, 1.5, 14.5, 13, 14.5);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext ctx) {
        Vec3d offset = state.getOffset(world, pos);
        return this.shape.withOffset(offset.x, offset.y, offset.z);
    }

    @Nonnull
    @Override
    public Effect getStewEffect() {
        return Effects.LUCK;
    }

    @Override
    public int getStewEffectDuration() {
        return 40;
    }

    @Override
    public int getExpDrop(BlockState state, IWorldReader world, BlockPos pos, int fortune, int silktouch) {
        if (silktouch == 0) {
            return 0;
        }
        if (fortune > 0) {
            return fortune * MathHelper.nextInt(RANDOM, 2, 5);
        }
        return MathHelper.nextInt(RANDOM, 1, 2);
    }

    @Override
    public PlantType getPlantType(IBlockReader world, BlockPos pos) {
        return PlantType.Cave;
    }

}
